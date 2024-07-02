package xyz.nucleoid.extras.lobby.particle;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class TateroidParticleSpawner extends SimpleTaterParticleSpawner {
    private final double defaultParticleColor;

    public TateroidParticleSpawner(ParticleEffect particleEffect, double defaultParticleColor) {
        super(particleEffect);

        this.defaultParticleColor = defaultParticleColor;
    }

    @Override
    protected double getParticleSpeed(TaterParticleContext context) {
        return context.getBlockEntity(NEBlocks.TATEROID_ENTITY)
                .map(blockEntity -> blockEntity.getParticleSpeed())
                .orElse(this.defaultParticleColor);
    }

    private Vec3d getPos(TaterParticleContext context) {
        var pos = context.getPos();

        if (context instanceof TaterParticleContext.Block) {
            return pos;
        }

        var box = context.getBox();
        var random = context.world().getRandom();

        double deltaX = box.getLengthX() / 2d;
        double deltaY = box.getLengthY() / 2d;
        double deltaZ = box.getLengthZ() / 2d;

        return pos.add(random.nextGaussian() * deltaX, random.nextGaussian() * deltaY, random.nextGaussian() * deltaZ);
    }

    @Override
    protected void spawn(TaterParticleContext context) {
        double particleSpeed = this.getParticleSpeed(context);

        if (particleSpeed < 0) {
            super.spawn(context);
            return;
        }

        var pos = this.getPos(context);
        var particleEffect = this.getParticleEffect(context);

        context.world().spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), 0, 1, 0, 0, particleSpeed);
    }
}
