package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

public class EntityEffectTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final EntityEffectTaterParticleSpawner DEFAULT = new EntityEffectTaterParticleSpawner(DEFAULT_PLAYER_PARTICLE_RATE, DEFAULT_BLOCK_PARTICLE_CHANCE);

    public static final MapCodec<EntityEffectTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PLAYER_PARTICLE_RATE_CODEC.forGetter(EntityEffectTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(EntityEffectTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, EntityEffectTaterParticleSpawner::new)
    );

    private final Random random = Random.createLocal();

    public EntityEffectTaterParticleSpawner(int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);
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
