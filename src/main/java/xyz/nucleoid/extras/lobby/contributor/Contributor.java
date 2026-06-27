package xyz.nucleoid.extras.lobby.contributor;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.storage.TagValueInput;
import xyz.nucleoid.extras.mixin.lobby.ArmorStandEntityAccessor;

import java.util.Optional;
import java.util.function.Consumer;

public record Contributor(String name, ContributorSocials socials, Optional<CompoundTag> statueNbt) implements Comparable<Contributor> {
    protected static final Codec<Contributor> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.STRING.fieldOf("name").forGetter(Contributor::name),
                ContributorSocials.CODEC.fieldOf("socials").forGetter(Contributor::socials),
                CompoundTag.CODEC.optionalFieldOf("statue_nbt").forGetter(Contributor::statueNbt)
        ).apply(instance, Contributor::new)
    );

    public Component getName() {
        return Component.literal(this.name);
    }

    public ItemStack createPlayerHead(GameProfile profile) {
        var playerHead = new ItemStack(Items.PLAYER_HEAD);
        writeSkullOwner(playerHead, profile);

        return playerHead;
    }

    public void fillEntity(MinecraftServer server, Entity entity) {
        if (this.statueNbt.isPresent()) {
            entity.load(TagValueInput.create(ProblemReporter.DISCARDING, server.registryAccess(), this.statueNbt.get()));
        }

        // Name
        entity.setCustomName(this.getName());
        entity.setCustomNameVisible(true);

        // Equipment
        var profile = this.createGameProfile(server);
        var playerHead = this.createPlayerHead(profile);

        if (entity instanceof Mob mob) {
            mob.setItemSlot(EquipmentSlot.HEAD, playerHead);
        } else if (entity instanceof ArmorStand armorStand) {
            armorStand.setItemSlot(EquipmentSlot.HEAD, playerHead);
        }

        this.loadGameProfileProperties(server, profile, fullProfile -> {
            writeSkullOwner(playerHead, fullProfile);
        });

        if (entity instanceof ArmorStand) {
            var accessor = (ArmorStandEntityAccessor) (Object) entity;
            accessor.callSetHideBasePlate(true);
            accessor.callSetShowArms(true);
        }
    }

    public GameProfile createGameProfile(MinecraftServer server) {
        var uuid = this.socials.minecraft();
        // Todo
        return new GameProfile(uuid, "");
    }

    public void loadGameProfileProperties(MinecraftServer server, GameProfile profile, Consumer<GameProfile> callback) {
        /*SkullBlockEntityAccessor.callFetchProfileWithTextures(profile).thenAccept(optional -> {
            optional.ifPresent(fullProfile -> {
                server.getUserCache().add(fullProfile);
                callback.accept(fullProfile);
            });
        });*/
    }

    @Override
    public int compareTo(Contributor o) {
        return this.name.compareToIgnoreCase(o.name);
    }

    public static void writeSkullOwner(ItemStack stack, GameProfile profile) {
        stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile));
    }
}
