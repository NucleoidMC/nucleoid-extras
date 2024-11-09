package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import xyz.nucleoid.extras.lobby.NEEntities;
import xyz.nucleoid.extras.lobby.entity.QuickArmorStandEntity;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class QuickArmorStandItem extends Item implements PolymerItem {
    public QuickArmorStandItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof ArmorStandEntity armorStandEntity) {
            var quickArmorStand = new QuickArmorStandEntity(armorStandEntity.getWorld());
            quickArmorStand.readNbt(armorStandEntity.writeNbt(new NbtCompound()));
            armorStandEntity.remove(Entity.RemovalReason.DISCARDED);
            quickArmorStand.getWorld().spawnEntity(quickArmorStand);
            return true;
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
            BlockPos blockPos = itemPlacementContext.getBlockPos();
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Box box = NEEntities.QUICK_ARMOR_STAND.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            if (world.isSpaceEmpty(box) && world.getOtherEntities(null, box).isEmpty()) {
                if (world instanceof ServerWorld serverWorld) {
                    var armorStandEntity = NEEntities.QUICK_ARMOR_STAND.spawnFromItemStack(serverWorld, itemStack, context.getPlayer(), blockPos, SpawnReason.SPAWN_ITEM_USE, true, true);
                    if (armorStandEntity == null) {
                        return ActionResult.FAIL;
                    }

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    armorStandEntity.refreshPositionAndAngles(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0F);
                    this.setRotations(armorStandEntity, world.random);
                    world.playSound(null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, armorStandEntity.getPos());
                }

                itemStack.decrement(1);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    private void setRotations(ArmorStandEntity stand, Random random) {
        EulerAngle eulerAngle = stand.getHeadRotation();
        float f = random.nextFloat() * 5.0F;
        float g = random.nextFloat() * 20.0F - 10.0F;
        EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
        stand.setHeadRotation(eulerAngle2);
        eulerAngle = stand.getBodyRotation();
        f = random.nextFloat() * 10.0F - 5.0F;
        eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
        stand.setBodyRotation(eulerAngle2);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.ARMOR_STAND;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false)));
    }
}
