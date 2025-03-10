package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;

public class LuckyTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final LuckyTaterParticleSpawner DEFAULT = new LuckyTaterParticleSpawner(DEFAULT_PLAYER_PARTICLE_RATE, DEFAULT_BLOCK_PARTICLE_CHANCE);

    public static final MapCodec<LuckyTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PLAYER_PARTICLE_RATE_CODEC.forGetter(LuckyTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(LuckyTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, LuckyTaterParticleSpawner::new)
    );

    public LuckyTaterParticleSpawner(int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);
    }

    @Override
    protected ParticleEffect getParticleEffect(TaterParticleContext context) {
        var random = context.world().getRandom();

        int fromColor = getRandomColor(random);
        int toColor = getRandomColor(random);

        int scale = random.nextInt(3);
        return new DustColorTransitionParticleEffect(fromColor, toColor, scale);
    }

    @Override
    public MapCodec<? extends LuckyTaterParticleSpawner> getCodec() {
        return CODEC;
    }

    private static int getRandomColor(Random random) {
        return random.nextInt() * 0xFFFFFF;
    }
}
