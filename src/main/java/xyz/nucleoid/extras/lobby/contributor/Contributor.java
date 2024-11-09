package xyz.nucleoid.extras.lobby.contributor;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import xyz.nucleoid.extras.mixin.lobby.ArmorStandEntityAccessor;

public record Contributor(String name, ContributorSocials socials, Optional<NbtCompound> statueNbt) implements Comparable<Contributor> {
    protected static final Codec<Contributor> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.STRING.fieldOf("name").forGetter(Contributor::name),
                ContributorSocials.CODEC.fieldOf("socials").forGetter(Contributor::socials),
                NbtCompound.CODEC.optionalFieldOf("statue_nbt").forGetter(Contributor::statueNbt)
        ).apply(instance, Contributor::new)
    );

    public Text getName() {
        return Text.literal(this.name);
    }

    public ItemStack createPlayerHead(GameProfile profile) {
        var playerHead = new ItemStack(Items.PLAYER_HEAD);
        writeSkullOwner(playerHead, profile);

        return playerHead;
    }

    public void fillEntity(MinecraftServer server, Entity entity) {
        if (this.statueNbt.isPresent()) {
            entity.readNbt(this.statueNbt.get());
        }

        // Name
        entity.setCustomName(this.getName());
        entity.setCustomNameVisible(true);

        // Equipment
        var profile = this.createGameProfile(server);
        var playerHead = this.createPlayerHead(profile);

        if (entity instanceof MobEntity mob) {
            mob.equipStack(EquipmentSlot.HEAD, playerHead);
        } else if (entity instanceof ArmorStandEntity armorStand) {
            armorStand.equipStack(EquipmentSlot.HEAD, playerHead);
        }

        this.loadGameProfileProperties(server, profile, fullProfile -> {
            writeSkullOwner(playerHead, fullProfile);
        });

        if (entity instanceof ArmorStandEntity) {
            var accessor = (ArmorStandEntityAccessor) (Object) entity;
            accessor.callSetHideBasePlate(true);
            accessor.callSetShowArms(true);
        }
    }

    public GameProfile createGameProfile(MinecraftServer server) {
        var uuid = this.socials.minecraft();

        return server.getUserCache().getByUuid(uuid).orElseGet(() -> {
            return new GameProfile(uuid, null);
        });
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
        stack.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));
    }
}
