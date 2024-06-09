package xyz.nucleoid.extras.mixin.debug;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;

@Mixin(Entity.class)
public class EntityMixin {
    @Unique
    private static final double MAX_REASONABLE_VELOCITY = MathHelper.square(100.0);

    @ModifyVariable(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"))
    private Vec3d clampVelocity(Vec3d velocity) {
        double lengthSq = velocity.lengthSquared();
        if (lengthSq > MAX_REASONABLE_VELOCITY) {
            double length = Math.sqrt(lengthSq);
            ExtrasErrorReporter.reportCustom(
                ExtrasErrorReporter.TOO_FAST,
                new RuntimeException(this + " got a velocity that was too large (" + length + " blocks/tick)")
            );
            return velocity.normalize().multiply(5.0);
        }
        return velocity;
    }
}
