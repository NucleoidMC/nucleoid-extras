package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.AbstractBlock;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawner;

public class CapsuleTaterBlock extends CubicPotatoBlock implements LuckyTaterDrop {
    private final int weight;

    public CapsuleTaterBlock(AbstractBlock.Settings settings, TaterParticleSpawner particleSpawner, int weight, String texture) {
        super(settings, particleSpawner, texture);

        this.weight = weight;
    }

    @Override
    public boolean isFickle() {
        return true;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}
