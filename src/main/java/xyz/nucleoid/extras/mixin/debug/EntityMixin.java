package xyz.nucleoid.extras.mixin.debug;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private static final double MAX_REASONABLE_VELOCITY = Mth.square(100.0);

    @ModifyVariable(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), argsOnly = true)
    private Vec3 clampVelocity(Vec3 velocity) {
        double lengthSq = velocity.lengthSqr();
        if (lengthSq > MAX_REASONABLE_VELOCITY) {
            double length = Math.sqrt(lengthSq);
            ExtrasErrorReporter.reportCustom(
                ExtrasErrorReporter.TOO_FAST,
                new RuntimeException(this + " got a velocity that was too large (" + length + " blocks/tick)")
            );
            return velocity.normalize().scale(5.0);
        }
        return velocity;
    }
}
