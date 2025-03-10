package xyz.nucleoid.extras.mixin.patches;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(Entity.class)
public class EntityMixin {
    /*
     * This fixes fireworks shoot by creative players breaking invulnerable entities
     */
    @ModifyExpressionValue(method = "isAlwaysInvulnerableTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isSourceCreativePlayer()Z"))
    private boolean extras$makeExceptionForExplosions(boolean original, @Local DamageSource damageSource) {
        return original && damageSource.isOf(DamageTypes.PLAYER_ATTACK);
    }
}
