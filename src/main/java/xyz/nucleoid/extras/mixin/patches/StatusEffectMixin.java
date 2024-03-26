package xyz.nucleoid.extras.mixin.patches;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(StatusEffect.class)
public class StatusEffectMixin {
    /*
     * This fixes healing potions killing creative (and survival) players
     */
    @ModifyVariable(method = "applyInstantEffect", at = @At("HEAD"), ordinal = 0)
    private int extras$patchAmplifier(int amplifier) {
        return MathHelper.clamp(amplifier, 0, 124);
    }
}
