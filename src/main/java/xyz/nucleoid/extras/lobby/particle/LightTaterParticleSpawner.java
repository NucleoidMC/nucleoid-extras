package xyz.nucleoid.extras.lobby.particle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class LightTaterParticleSpawner extends MarkerTaterParticleSpawner {
    public static final TaterParticleSpawner INSTANCE = new LightTaterParticleSpawner();

    private LightTaterParticleSpawner() {
        super();
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

    private static BlockState getLightState(int level) {
        return Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, level);
    }

    private static ParticleEffect getLightParticle(int level) {
        return new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, getLightState(level));
    }
}
