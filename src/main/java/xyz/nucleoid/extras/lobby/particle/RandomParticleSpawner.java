package xyz.nucleoid.extras.lobby.particle;

import java.util.function.Function;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;

public class RandomParticleSpawner extends TaterParticleSpawner {
    private final Function<Random, CubicPotatoBlock> taterSupplier;

    public RandomParticleSpawner(Function<Random, CubicPotatoBlock> taterSupplier) {
        this.taterSupplier = taterSupplier;
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        var tater = this.taterSupplier.apply(context.world().getRandom());
        return tater.getParticleSpawner().getParticleEffect(context);
    }
}
