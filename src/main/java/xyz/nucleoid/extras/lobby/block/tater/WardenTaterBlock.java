package xyz.nucleoid.extras.lobby.block.tater;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import xyz.nucleoid.extras.tag.NEBlockTags;

public class WardenTaterBlock extends CubicPotatoBlock {
    private static final int BOX_SIZE = 16;
    private static final int ARRIVAL_TICKS = SharedConstants.TICKS_PER_SECOND;

    public WardenTaterBlock(Settings settings, String texture) {
        super(settings, (ParticleEffect) null, texture);
    }

    @Override
    public ParticleEffect getBlockParticleEffect(BlockState state, ServerWorld world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return getTaterVibrationParticleEffect(pos, world);
    }

    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public ParticleEffect getPlayerParticleEffect(ServerPlayerEntity player) {
        BlockPos pos = BlockPos.ofFloored(player.getX(), player.getEyeY() - 0.2, player.getZ());
        return getTaterVibrationParticleEffect(pos, player.getServerWorld());
    }

    @Override
    public int getPlayerParticleRate(ServerPlayerEntity player) {
        return ARRIVAL_TICKS;
    }

    @Override
    public void spawnPlayerParticles(ServerPlayerEntity player) {;
        double x = player.getX();
        double y = player.getEyeY() - 0.2;
        double z = player.getZ();

        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.getServerWorld().spawnParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static ParticleEffect getTaterVibrationParticleEffect(BlockPos pos, ServerWorld world) {
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
        return state.getBlock() instanceof TinyPotatoBlock && !state.isIn(NEBlockTags.NON_VIBRATING_TATERS);
    }
}
