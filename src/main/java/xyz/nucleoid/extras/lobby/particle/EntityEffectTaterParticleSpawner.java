package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

public class EntityEffectTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final EntityEffectTaterParticleSpawner INSTANCE = new EntityEffectTaterParticleSpawner();

    public static final MapCodec<EntityEffectTaterParticleSpawner> CODEC = MapCodec.unit(INSTANCE);

    private final Random random = Random.createLocal();

    private EntityEffectTaterParticleSpawner() {
        super();
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        float r = (float) (this.random.nextGaussian() * 0.2);
        float g = (float) (this.random.nextGaussian() * 0.2);
        float b = (float) (this.random.nextGaussian() * 0.2);

        return EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b);
    }

    @Override
    public MapCodec<? extends EntityEffectTaterParticleSpawner> getCodec() {
        return CODEC;
    }
}
