package xyz.nucleoid.extras.mixin.patches;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public class EntityMixin {
    @Shadow private boolean invulnerable;

    /*
     * This fixes fireworks shoot by creative players breaking invulnerable entities
     */
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void extras$makeExceptionForExplosions(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (this.invulnerable && damageSource.isIn(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(true);
        }
    }
}
