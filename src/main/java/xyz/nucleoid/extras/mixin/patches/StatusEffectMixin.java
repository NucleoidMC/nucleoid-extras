package xyz.nucleoid.extras.mixin.patches;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(MobEffect.class)
public class StatusEffectMixin {
    /*
     * This fixes healing potions killing creative (and survival) players
     */
    @ModifyVariable(method = "applyInstantaneousEffect", at = @At("HEAD"), ordinal = 0)
    private int extras$patchAmplifier(int amplifier) {
        return Mth.clamp(amplifier, 0, 124);
    }
}
