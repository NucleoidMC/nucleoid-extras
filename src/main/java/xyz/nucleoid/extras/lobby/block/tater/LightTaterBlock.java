package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LightTaterBlock extends MarkerTaterBlock {
    public LightTaterBlock(Properties settings, String texture) {
        super(settings, Blocks.LIGHT, texture);
    }

    @Override
    public ParticleOptions getBlockParticleEffect(BlockState state, ServerLevel world, BlockPos pos, Player player, BlockHitResult hit) {
        return getLightParticle(world.getMaxLocalRawBrightness(pos));
    }

    @Override
    public double getPlayerParticleYOffset() {
        return 3;
    }

    @Override
    public ParticleOptions getPlayerParticleEffect(ServerPlayer player) {
        BlockPos pos = BlockPos.containing(player.getX(), player.getY() + this.getPlayerParticleYOffset(), player.getZ());

        return getLightParticle(player.level().getMaxLocalRawBrightness(pos));
    }

    private static BlockState getLightState(int level) {
        return Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, level);
    }

    private static ParticleOptions getLightParticle(int level) {
        return new BlockParticleOption(ParticleTypes.BLOCK_MARKER, getLightState(level));
    }
}
