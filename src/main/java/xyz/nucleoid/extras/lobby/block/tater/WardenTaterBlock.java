package xyz.nucleoid.extras.lobby.block.tater;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;

public class WardenTaterBlock extends CubicPotatoBlock {
    private static final int BOX_SIZE = 16;
    private static final int ARRIVAL_TICKS = SharedConstants.TICKS_PER_SECOND;

    public WardenTaterBlock(Settings settings, String texture) {
        super(settings, (ParticleEffect) null, texture);
    }

    @Override
    public ParticleEffect getBlockParticleEffect(BlockState state, ServerWorld world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return getTaterVibrationParticleEffect(pos, new BlockPositionSource(pos), world);
    }

    @Override
    public ParticleEffect getPlayerParticleEffect(ServerPlayerEntity player) {
        BlockPos pos = BlockPos.ofFloored(player.getX(), player.getEyeY(), player.getZ());
        return getTaterVibrationParticleEffect(pos, new EntityPositionSource(player, 0), player.getServerWorld());
    }

    @Override
    public int getPlayerParticleRate(ServerPlayerEntity player) {
        return ARRIVAL_TICKS;
    }

    private static ParticleEffect getTaterVibrationParticleEffect(BlockPos pos, PositionSource source, ServerWorld world) {
        LongList taters = new LongArrayList();

        int range = (int) (BOX_SIZE / 2d);
        for (BlockPos taterPos : BlockPos.iterateOutwards(pos, range, range, range)) {
            BlockState state = world.getBlockState(taterPos);
            if (isVibrationTater(state)) {
                taters.add(taterPos.asLong());
            }
        }

        if (taters.isEmpty()) {
            return null;
        }

        int index = world.getRandom().nextInt(taters.size());
        BlockPos taterPos = BlockPos.fromLong(taters.getLong(index));

        return new VibrationParticleEffect(new BlockPositionSource(taterPos), (int) Math.floor(Math.sqrt(pos.getSquaredDistance(taterPos))));
    }

    private static boolean isVibrationTater(BlockState state) {
        if (state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
            return false;
        }

        Block block = state.getBlock();
        return block instanceof CubicPotatoBlock && !(block instanceof WardenTaterBlock);
    }
}
