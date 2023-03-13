package xyz.nucleoid.extras.lobby.contributor;

import java.util.Optional;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import xyz.nucleoid.extras.mixin.lobby.ArmorStandEntityAccessor;

public record Contributor(String name, ContributorSocials socials, Optional<NbtCompound> statueNbt) {
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

    public ItemStack createPlayerHead(MinecraftServer server) {
        var playerHead = new ItemStack(Items.PLAYER_HEAD);
        var nbt = playerHead.getOrCreateNbt();

        var profile = new GameProfile(this.socials.minecraft(), null);

        if (profile.getId() != null && server != null) {
            profile = server.getSessionService().fillProfileProperties(profile, false);
            nbt.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), profile));
        } else {
            nbt.putString("SkullOwner", profile.getName());
        }

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
        entity.equipStack(EquipmentSlot.HEAD, this.createPlayerHead(server));

        if (entity instanceof ArmorStandEntity) {
            var accessor = (ArmorStandEntityAccessor) (Object) entity;
            accessor.callSetHideBasePlate(true);
            accessor.callSetShowArms(true);
        }
    }
}
