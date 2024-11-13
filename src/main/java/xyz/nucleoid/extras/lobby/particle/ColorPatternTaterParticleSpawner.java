package xyz.nucleoid.extras.lobby.particle;

import java.util.stream.IntStream;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;

public class ColorPatternTaterParticleSpawner extends DynamicTaterParticleSpawner {
    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterParticleSpawner(int[] pattern) {
        this.particleEffects = IntStream.of(pattern).mapToObj(color ->
            new DustParticleEffect(color, 1)
        ).toArray(ParticleEffect[]::new);
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffects[(context.getTime() / 10) % this.particleEffects.length];
    }
}
