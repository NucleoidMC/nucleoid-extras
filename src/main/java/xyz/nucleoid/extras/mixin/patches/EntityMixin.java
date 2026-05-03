package xyz.nucleoid.extras.mixin.patches;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(Entity.class)
public class EntityMixin {
    /*
     * This fixes fireworks shoot by creative players breaking invulnerable entities
     */
    @ModifyExpressionValue(method = "isInvulnerableToBase", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;isCreativePlayer()Z"))
    private boolean extras$makeExceptionForExplosions(boolean original, @Local DamageSource damageSource) {
        return original && damageSource.is(DamageTypes.PLAYER_ATTACK);
    }
}
