package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class LightTaterParticleSpawner extends MarkerTaterParticleSpawner {
    public static final LightTaterParticleSpawner DEFAULT = new LightTaterParticleSpawner(MARKER_PLAYER_PARTICLE_RATE, DEFAULT_BLOCK_PARTICLE_CHANCE);

    public static final MapCodec<LightTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PLAYER_PARTICLE_RATE_CODEC.forGetter(LightTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(LightTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, LightTaterParticleSpawner::new)
    );

    public LightTaterParticleSpawner(int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);
    }

    @Override
    protected double getPlayerParticleOffsetY() {
        return 3;
    }

    private BlockPos getLightSamplePos(TaterParticleContext context) {
        var pos = context.getPos();
        double offsetY = context instanceof TaterParticleContext.Player ? this.getPlayerParticleOffsetY() : 0;

        return BlockPos.ofFloored(pos.getX(), pos.getY() + offsetY, pos.getZ());
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        var pos = this.getLightSamplePos(context);
        int level = context.world().getLightLevel(pos);

        return getLightParticle(level);
    }

    @Override
    public MapCodec<? extends LightTaterParticleSpawner> getCodec() {
        return CODEC;
    }

    private static BlockState getLightState(int level) {
        return Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, level);
    }

    private static ParticleEffect getLightParticle(int level) {
        return new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, getLightState(level));
    }
}
