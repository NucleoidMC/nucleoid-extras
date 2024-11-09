package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;

import java.util.Arrays;

public class ColorPatternTaterBlock extends CubicPotatoBlock {
    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterBlock(Settings settings, int[] pattern, String texture) {
        super(settings, (ParticleEffect) null, texture);

        this.particleEffects = Arrays.stream(pattern).mapToObj(color ->
            new DustParticleEffect(color, 1)
        ).toArray(ParticleEffect[]::new);
    }

    @Override
    public ParticleEffect getParticleEffect(int time) {
        return this.particleEffects[(time / 10) % this.particleEffects.length];
    }
}
