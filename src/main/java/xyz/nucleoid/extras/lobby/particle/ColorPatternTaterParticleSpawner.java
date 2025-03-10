package xyz.nucleoid.extras.lobby.particle;

import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import xyz.nucleoid.codecs.MoreCodecs;

public class ColorPatternTaterParticleSpawner extends DynamicTaterParticleSpawner {
    private static final Codec<ParticleEffect[]> PARTICLES_CODEC = MoreCodecs.listToArray(ParticleTypes.TYPE_CODEC.listOf(), ParticleEffect[]::new);

    public static final MapCodec<ColorPatternTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PARTICLES_CODEC.fieldOf("particles").forGetter(spawner -> spawner.particleEffects),
                PLAYER_PARTICLE_RATE_CODEC.forGetter(ColorPatternTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(ColorPatternTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, ColorPatternTaterParticleSpawner::new)
    );

    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterParticleSpawner(ParticleEffect[] particleEffects, int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);

        this.particleEffects = particleEffects;
    }

    public ColorPatternTaterParticleSpawner(int[] pattern) {
        this(IntStream.of(pattern).mapToObj(color ->
            new DustParticleEffect(color, 1)
        ).toArray(ParticleEffect[]::new), DEFAULT_PLAYER_PARTICLE_RATE, DEFAULT_BLOCK_PARTICLE_CHANCE);
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffects[(context.getTime() / 10) % this.particleEffects.length];
    }

    @Override
    public MapCodec<? extends ColorPatternTaterParticleSpawner> getCodec() {
        return CODEC;
    }
}
