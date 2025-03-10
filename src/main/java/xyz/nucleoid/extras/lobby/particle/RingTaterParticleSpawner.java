package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class RingTaterParticleSpawner extends SimpleTaterParticleSpawner {
    public static final MapCodec<RingTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PARTICLE_CODEC.forGetter(RingTaterParticleSpawner::getParticleEffect),
                PLAYER_PARTICLE_RATE_CODEC.forGetter(RingTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(RingTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, RingTaterParticleSpawner::new)
    );

    private static final int PARTICLE_COUNT = 8;

    private static final double BLOCK_PARTICLE_RADIUS = 0.8;
    private static final double PLAYER_PARTICLE_RADIUS = 0.5;

    public RingTaterParticleSpawner(ParticleEffect particleEffect, int playerParticleRate, int blockParticleChance) {
        super(particleEffect, playerParticleRate, blockParticleChance);
    }

    @Override
    protected void spawn(TaterParticleContext context) {
        var pos = context.getPos();
        double y = context instanceof TaterParticleContext.Player playerContext ? playerContext.player().getBodyY(0.5) : pos.getY();

        double radius = context instanceof TaterParticleContext.Player playerContext ? (playerContext.player().getWidth() / 2 + PLAYER_PARTICLE_RADIUS) : BLOCK_PARTICLE_RADIUS;
        double centerAngle = context instanceof TaterParticleContext.Player playerContext ? (playerContext.player().getYaw() * MathHelper.RADIANS_PER_DEGREE) : 0;

        var particleEffect = this.getParticleEffect(context);

        if (particleEffect != null) {
            this.spawnParticlesAround(context.world(), particleEffect, pos.getX(), radius, y, pos.getZ(), radius, centerAngle);
        }
    }

    @Override
    public MapCodec<? extends RingTaterParticleSpawner> getCodec() {
        return CODEC;
    }

    private void spawnParticlesAround(ServerWorld world, ParticleEffect particleEffect, double centerX, double radiusX, double y, double centerZ, double radiusZ, double centerAngle) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = i / (double) PARTICLE_COUNT * Math.PI * 2 + centerAngle;

            double x = centerX + Math.cos(angle) * radiusX;
            double z = centerZ + Math.sin(angle) * radiusZ;

            world.spawnParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public static TaterParticleSpawner ofDust(int color) {
        return new RingTaterParticleSpawner(new DustParticleEffect(color, 1), SimpleTaterParticleSpawner.DEFAULT_PLAYER_PARTICLE_RATE, SimpleTaterParticleSpawner.DEFAULT_BLOCK_PARTICLE_CHANCE);
    }
}
