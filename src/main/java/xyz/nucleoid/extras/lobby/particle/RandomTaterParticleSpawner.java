package xyz.nucleoid.extras.lobby.particle;

import java.util.function.Function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.extras.lobby.block.tater.CorruptaterBlock;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;

public class RandomTaterParticleSpawner extends DynamicTaterParticleSpawner {
    private static final MapCodec<Function<Random, CubicPotatoBlock>> TATER_SUPPLIER_CODEC = MapCodec.unit(CorruptaterBlock::getTater);

    public static final MapCodec<RandomTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                TATER_SUPPLIER_CODEC.forGetter(spawner -> spawner.taterSupplier),
                PLAYER_PARTICLE_RATE_CODEC.forGetter(RandomTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(RandomTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, RandomTaterParticleSpawner::new)
    );

    private final Function<Random, CubicPotatoBlock> taterSupplier;

    public RandomTaterParticleSpawner(Function<Random, CubicPotatoBlock> taterSupplier, int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);

        this.taterSupplier = taterSupplier;
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        var tater = this.taterSupplier.apply(context.world().getRandom());
        return tater.getParticleSpawner().getParticleEffect(context);
    }

    @Override
    public MapCodec<? extends RandomTaterParticleSpawner> getCodec() {
        return CODEC;
    }
}
