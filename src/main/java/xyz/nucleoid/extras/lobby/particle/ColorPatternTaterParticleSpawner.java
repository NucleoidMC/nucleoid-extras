package xyz.nucleoid.extras.lobby.particle;

import java.util.stream.Stream;

import org.joml.Vector3f;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;

public class ColorPatternTaterParticleSpawner extends DynamicTaterParticleSpawner {
    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterParticleSpawner(Vector3f[] pattern) {
        this.particleEffects = Stream.of(pattern).map(color ->
            new DustParticleEffect(color, 1)
        ).toArray(ParticleEffect[]::new);
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffects[(context.getTime() / 10) % this.particleEffects.length];
    }
}
