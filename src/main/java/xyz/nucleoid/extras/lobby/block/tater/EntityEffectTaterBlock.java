package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public class EntityEffectTaterBlock extends CubicPotatoBlock {
    private final RandomSource random = RandomSource.createThreadLocalInstance();

    public EntityEffectTaterBlock(Properties settings, String texture) {
        super(settings, (ParticleOptions) null, texture);
    }

    @Override
    public ParticleOptions getParticleEffect(int time) {
        float r = (float) (this.random.nextGaussian() * 0.2);
        float g = (float) (this.random.nextGaussian() * 0.2);
        float b = (float) (this.random.nextGaussian() * 0.2);

        return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, r, g, b);
    }
}
