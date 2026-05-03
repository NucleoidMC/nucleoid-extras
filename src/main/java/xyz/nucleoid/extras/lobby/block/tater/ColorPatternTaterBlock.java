package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;

import java.util.Arrays;

public class ColorPatternTaterBlock extends CubicPotatoBlock {
    private final ParticleOptions[] particleEffects;

    public ColorPatternTaterBlock(Properties settings, int[] pattern, String texture) {
        super(settings, (ParticleOptions) null, texture);

        this.particleEffects = Arrays.stream(pattern).mapToObj(color ->
            new DustParticleOptions(color, 1)
        ).toArray(ParticleOptions[]::new);
    }

    @Override
    public ParticleOptions getParticleEffect(int time) {
        return this.particleEffects[(time / 10) % this.particleEffects.length];
    }
}
