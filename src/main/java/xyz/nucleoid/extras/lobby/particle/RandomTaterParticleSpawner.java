package xyz.nucleoid.extras.lobby.particle;

import java.util.function.Function;

import com.mojang.serialization.MapCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.extras.lobby.block.tater.CorruptaterBlock;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;

public class RandomTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final MapCodec<RandomTaterParticleSpawner> CODEC = MapCodec.unit(() -> {
        return new RandomTaterParticleSpawner(CorruptaterBlock::getTater);
    });

    private final Function<Random, CubicPotatoBlock> taterSupplier;

    public RandomTaterParticleSpawner(Function<Random, CubicPotatoBlock> taterSupplier) {
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
