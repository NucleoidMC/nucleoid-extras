package xyz.nucleoid.extras.lobby.block;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import eu.pb4.holograms.api.elements.clickable.EntityHologramElement;
import eu.pb4.holograms.api.holograms.AbstractHologram;
import eu.pb4.holograms.api.holograms.WorldHologram;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.contributor.Contributor;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;
import xyz.nucleoid.extras.lobby.item.TaterBoxItem;
import xyz.nucleoid.extras.mixin.lobby.LivingEntityAccessor;
import xyz.nucleoid.extras.util.PagedGui;

public class ContributorStatueBlockEntity extends BlockEntity {
    protected static final String CONTRIBUTOR_ID_KEY = "contributor_id";

    private static final Text GUI_TITLE = Text.translatable("text.nucleoid_extras.contributor_statue.title");

    private static final List<Function<World, Entity>> SPOOKY_ENTITIES = ImmutableList.of(
        ZombieEntity::new,
        world -> new DrownedEntity(EntityType.DROWNED, world),
        world -> new HuskEntity(EntityType.HUSK, world),
        world -> new SkeletonEntity(EntityType.SKELETON, world),
        world -> new StrayEntity(EntityType.STRAY, world)
    );

    private String contributorId = "";

    private WorldHologram hologram;

    public ContributorStatueBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.CONTRIBUTOR_STATUE_ENTITY, pos, state);
    }

    private Entity getHologramEntity() {
        LocalDate date = LocalDate.now();

        int month = date.get(ChronoField.MONTH_OF_YEAR);
        int day = date.get(ChronoField.DAY_OF_MONTH);

        if (month == 10 && day == 31) {
            int index = Math.floorMod(this.contributorId.hashCode(), SPOOKY_ENTITIES.size());
            return SPOOKY_ENTITIES.get(index).apply(world);
        }

        return new ArmorStandEntity(world, 0, 0, 0);
    }

    public void spawnHolograms() {
        if (this.hologram != null) return;

        var contributor = ContributorData.getContributor(this.contributorId);
        if (contributor == null) return;

        double x = this.pos.getX() + 0.5;
        double y = this.pos.getY() + 1;
        double z = this.pos.getZ() + 0.5;

        var world = (ServerWorld) this.world;
        this.hologram = new WorldHologram(world, new Vec3d(x, y, z));

        var entity = this.getHologramEntity();
        contributor.fillEntity(world.getServer(), entity);

        entity.setYaw(entity.getYaw() + this.getCachedState().get(ContributorStatueBlock.FACING).asRotation());
        entity.setHeadYaw(entity.getYaw());
        entity.setBodyYaw(entity.getYaw());

        this.hologram.addElement(new EquipmentEntityHologramElement(entity));
        this.hologram.show();
    }

    public void removeHolograms() {
        if (this.hologram != null) {
            this.hologram.hide();
            this.hologram = null;
        }
    }

    private void selectContributor(ServerPlayerEntity player, String id) {
        if (this.contributorId.equals(id)) return;

        this.contributorId = id;
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);

        this.removeHolograms();
        this.spawnHolograms();

        this.markDirty();
    }

    protected void openEditScreen(ServerPlayerEntity player) {
        var server = player.getServer();

        List<GuiElementInterface> elements = ContributorData.getContributors()
                .stream()
                .sorted((a, b) -> {
                    return a.getValue().compareTo(b.getValue());
                })
                .map(entry -> {
                    var id = entry.getKey();
                    var contributor = entry.getValue();

                    var profile = contributor.createGameProfile(server);

                    var builder = GuiElementBuilder.from(contributor.createPlayerHead(profile))
                        .setName(contributor.getName())
                        .setCallback(() -> {
                            this.selectContributor(player, id);
                        });

                    var element = builder.build();

                    contributor.loadGameProfileProperties(server, profile, fullProfile -> {
                        Contributor.writeSkullOwner(element.getItemStack(), fullProfile);
                    });

                    return element;
                })
                .collect(Collectors.toList());

        elements.add(0, new GuiElementBuilder(Items.BARRIER)
            .setName(TaterBoxItem.NONE_TEXT)
            .setCallback(() -> {
                this.selectContributor(player, "");
            })
            .build());

        var gui = PagedGui.of(player, elements);
        gui.setTitle(GUI_TITLE);

        gui.open();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setCachedState(BlockState state) {
        super.setCachedState(state);

        this.removeHolograms();
        this.spawnHolograms();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        this.removeHolograms();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.contributorId = nbt.getString(CONTRIBUTOR_ID_KEY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString(CONTRIBUTOR_ID_KEY, this.contributorId);
    }

    private static class EquipmentEntityHologramElement extends EntityHologramElement {
        public EquipmentEntityHologramElement(Entity entity) {
            super(entity);
        }

        @Override
        public void createSpawnPackets(ServerPlayerEntity player, AbstractHologram hologram) {
            super.createSpawnPackets(player, hologram);

            if (this.entity instanceof LivingEntity livingEntity) {
                ArrayList<Pair<EquipmentSlot, ItemStack>> equipmentList = Lists.newArrayList();

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = livingEntity.getEquippedStack(slot);

                    if (!stack.isEmpty()) {
                        equipmentList.add(Pair.of(slot, stack.copy()));
                    }
                }

                if (!equipmentList.isEmpty()) {
                    Packet<?> packet = new EntityEquipmentUpdateS2CPacket(this.entity.getId(), equipmentList);
                    player.networkHandler.sendPacket(packet);
                }
            }
        }

        @Override
        public void onTick(AbstractHologram hologram) {
            super.onTick(hologram);

            if (this.entity instanceof LivingEntityAccessor accessor) {
                var equipmentChanges = accessor.callGetEquipmentChanges();

                if (equipmentChanges != null && !equipmentChanges.isEmpty()) {
                    var list = new ArrayList<Pair<EquipmentSlot, ItemStack>>(equipmentChanges.size());

                    equipmentChanges.forEach((slot, stack) -> {
                        var stackCopy = stack.copy();
                        list.add(Pair.of(slot, stackCopy));

                        switch (slot.getType()) {
                            case HAND -> accessor.callSetSyncedHandStack(slot, stackCopy);
                            case ARMOR -> accessor.callSetSyncedArmorStack(slot, stackCopy);
                        }
                    });

                    var packet = new EntityEquipmentUpdateS2CPacket(this.entity.getId(), list);

                    for (ServerPlayerEntity player : hologram.getPlayerSet()) {
                        player.networkHandler.sendPacket(packet);
                    }
                }
            }
        }
    }
}
