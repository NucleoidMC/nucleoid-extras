package xyz.nucleoid.extras.lobby.particle;

import org.jetbrains.annotations.Nullable;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;

public abstract class TaterParticleSpawner {
    protected boolean shouldSpawn(TaterParticleContext context) {
        return true;
    }

    @Nullable
    protected abstract ParticleEffect getParticleEffect(TaterParticleContext context);

    protected double getParticleSpeed(TaterParticleContext context) {
        return 0.2;
    }

    public final void trySpawn(TaterParticleContext context) {
        if (this.shouldSpawn(context)) {
            this.spawn(context);
        }
    }

    protected void spawn(TaterParticleContext context) {
        var box = context.getBox();

        double deltaX = box.getLengthX() / 2d;
        double deltaY = box.getLengthY() / 2d;
        double deltaZ = box.getLengthZ() / 2d;

        double x = MathHelper.lerp(0.5, box.minX, box.maxX);
        double y = MathHelper.lerp(0.5, box.minY, box.maxY);
        double z = MathHelper.lerp(0.5, box.minZ, box.maxZ);

        var particleEffect = this.getParticleEffect(context);
        double particleSpeed = this.getParticleSpeed(context);

        if (particleEffect != null) {
            context.world().spawnParticles(particleEffect, x, y, z, 1, deltaX, deltaY, deltaZ, particleSpeed);
        }
    }
}
