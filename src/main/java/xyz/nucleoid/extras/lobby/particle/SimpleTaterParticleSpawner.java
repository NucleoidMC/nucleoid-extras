package xyz.nucleoid.extras.lobby.particle;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.DyeColor;

public class SimpleTaterParticleSpawner extends DynamicTaterParticleSpawner {
    private final ParticleEffect particleEffect;

    public SimpleTaterParticleSpawner(ParticleEffect particleEffect, int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);

        this.particleEffect = particleEffect;
    }

    public SimpleTaterParticleSpawner(ParticleEffect particleEffect, int playerParticleRate) {
        this(particleEffect, playerParticleRate, DEFAULT_BLOCK_PARTICLE_CHANCE);
    }

    public SimpleTaterParticleSpawner(ParticleEffect particleEffect) {
        this(particleEffect, DEFAULT_PLAYER_PARTICLE_RATE);
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffect;
    }

    public static TaterParticleSpawner ofDust(int color) {
        return new SimpleTaterParticleSpawner(new DustParticleEffect(color, 1));
    }

    public static TaterParticleSpawner ofDust(DyeColor color) {
        return ofDust(color.getSignColor());
    }
}
