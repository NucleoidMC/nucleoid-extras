package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;

public abstract class MarkerTaterParticleSpawner extends DynamicTaterParticleSpawner {
    private static final int PLAYER_PARTICLE_RATE = 12;

    public MarkerTaterParticleSpawner() {
        super(PLAYER_PARTICLE_RATE);
    }

    protected double getPlayerParticleOffsetY() {
        return 0.5;
    }

    @Override
    protected void spawn(TaterParticleContext context) {
        var pos = context.getPos();
        double offsetY = context instanceof TaterParticleContext.Player ? this.getPlayerParticleOffsetY() : 0.65;

        double x = pos.getX();
        double y = pos.getY() + offsetY;
        double z = pos.getZ();

        var particleEffect = this.getParticleEffect(context);

        if (particleEffect != null) {
            context.world().spawnParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public abstract MapCodec<? extends MarkerTaterParticleSpawner> getCodec();
}
