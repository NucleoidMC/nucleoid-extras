package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

public class SimpleMarkerTaterParticleSpawner extends MarkerTaterParticleSpawner {
    public static final MapCodec<SimpleMarkerTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                Registries.BLOCK.getCodec().fieldOf("block").forGetter(spawner -> spawner.block),
                PLAYER_PARTICLE_RATE_CODEC.forGetter(SimpleMarkerTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(SimpleMarkerTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, SimpleMarkerTaterParticleSpawner::new)
    );

    private final Block block;
    private final ParticleEffect particleEffect;

    public SimpleMarkerTaterParticleSpawner(Block block, int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);

        this.block = block;
        this.particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, block.getDefaultState());
    }

    @Override
    protected ParticleEffect getParticleEffect(TaterParticleContext context) {
        return this.particleEffect;
    }

    @Override
    public MapCodec<? extends SimpleMarkerTaterParticleSpawner> getCodec() {
        return CODEC;
    }
}
