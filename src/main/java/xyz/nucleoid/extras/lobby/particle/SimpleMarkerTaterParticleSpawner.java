package xyz.nucleoid.extras.lobby.particle;

import net.minecraft.block.Block;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class SimpleMarkerTaterParticleSpawner extends MarkerTaterParticleSpawner {
    private final ParticleEffect particleEffect;

    public SimpleMarkerTaterParticleSpawner(Block block) {
        this.particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, block.getDefaultState());
    }

    @Override
    protected ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffect;
    }
}
