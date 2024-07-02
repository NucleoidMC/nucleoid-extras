package xyz.nucleoid.extras.lobby.particle;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import org.joml.Vector3f;

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

    public static TaterParticleSpawner ofDust(Vector3f color) {
        return new SimpleTaterParticleSpawner(new DustParticleEffect(color, 1));
    }

    public static TaterParticleSpawner ofDust(DyeColor color) {
        return ofDust(Vec3d.unpackRgb(color.getSignColor()).toVector3f());
    }
}
