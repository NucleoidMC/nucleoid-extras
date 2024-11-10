package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

public class EntityEffectTaterBlock extends CubicPotatoBlock {
    private final Random random = Random.createLocal();

    public EntityEffectTaterBlock(Settings settings, String texture) {
        super(settings, (ParticleEffect) null, texture);
    }

    @Override
    public ParticleEffect getParticleEffect(int time) {
        float r = (float) (this.random.nextGaussian() * 0.2);
        float g = (float) (this.random.nextGaussian() * 0.2);
        float b = (float) (this.random.nextGaussian() * 0.2);

        return EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b);
    }
}
