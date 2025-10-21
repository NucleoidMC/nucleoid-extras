package xyz.nucleoid.extras.mixin.compat;

import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(GameProfile.class)
public abstract class GameProfileMixin {
    @Shadow
    public abstract String name();

    @Shadow
    @Final
    private UUID id;

    public String getName() {
        return this.name();
    }

    public UUID getId() {
        return this.id;
    }
}
