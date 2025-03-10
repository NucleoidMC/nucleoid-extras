package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.DyeColor;

public class SimpleTaterParticleSpawner extends DynamicTaterParticleSpawner {
    protected static final MapCodec<ParticleEffect> PARTICLE_CODEC = ParticleTypes.TYPE_CODEC.fieldOf("particle");

    public static final MapCodec<SimpleTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PARTICLE_CODEC.forGetter(SimpleTaterParticleSpawner::getParticleEffect),
                PLAYER_PARTICLE_RATE_CODEC.forGetter(SimpleTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(SimpleTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, SimpleTaterParticleSpawner::new)
    );

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

    protected final ParticleEffect getParticleEffect() {
        return this.particleEffect;
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffect;
    }

    @Override
    public MapCodec<? extends SimpleTaterParticleSpawner> getCodec() {
        return CODEC;
    }

    public static TaterParticleSpawner ofDust(int color) {
        return new SimpleTaterParticleSpawner(new DustParticleEffect(color, 1));
    }

    public static TaterParticleSpawner ofDust(DyeColor color) {
        return ofDust(color.getSignColor());
    }
}
