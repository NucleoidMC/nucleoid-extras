package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class LightTaterBlock extends MarkerTaterBlock {
    public LightTaterBlock(Settings settings, String texture) {
        super(settings, Blocks.LIGHT, texture);
    }

    @Override
    public ParticleEffect getBlockParticleEffect(BlockState state, ServerWorld world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return getLightParticle(world.getLightLevel(pos));
    }

    @Override
    public double getPlayerParticleYOffset() {
        return 3;
    }

    @Override
    public ParticleEffect getPlayerParticleEffect(ServerPlayerEntity player) {
        BlockPos pos = BlockPos.ofFloored(player.getX(), player.getY() + this.getPlayerParticleYOffset(), player.getZ());

        return getLightParticle(player.getWorld().getLightLevel(pos));
    }

    private static BlockState getLightState(int level) {
        return Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, level);
    }

    private static ParticleEffect getLightParticle(int level) {
        return new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, getLightState(level));
    }
}
