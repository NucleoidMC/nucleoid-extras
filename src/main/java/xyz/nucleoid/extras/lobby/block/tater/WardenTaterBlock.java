package xyz.nucleoid.extras.lobby.block.tater;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.extras.tag.NEBlockTags;

public class WardenTaterBlock extends CubicPotatoBlock {
    private static final int BOX_SIZE = 16;
    private static final int ARRIVAL_TICKS = SharedConstants.TICKS_PER_SECOND;

    public WardenTaterBlock(Properties settings, String texture) {
        super(settings, (ParticleOptions) null, texture);
    }

    @Override
    public ParticleOptions getBlockParticleEffect(BlockState state, ServerLevel world, BlockPos pos, Player player, BlockHitResult hit) {
        return getTaterVibrationParticleEffect(pos, world);
    }

    public void spawnBlockParticles(ServerLevel world, BlockPos pos, ParticleOptions particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.sendParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public ParticleOptions getPlayerParticleEffect(ServerPlayer player) {
        BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY() - 0.2, player.getZ());
        return getTaterVibrationParticleEffect(pos, player.level());
    }

    @Override
    public int getPlayerParticleRate(ServerPlayer player) {
        return ARRIVAL_TICKS;
    }

    @Override
    public void spawnPlayerParticles(ServerPlayer player) {;
        double x = player.getX();
        double y = player.getEyeY() - 0.2;
        double z = player.getZ();

        ParticleOptions particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.level().sendParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static ParticleOptions getTaterVibrationParticleEffect(BlockPos pos, ServerLevel world) {
        LongList taters = new LongArrayList();

        int range = (int) (BOX_SIZE / 2d);
        for (BlockPos taterPos : BlockPos.withinManhattan(pos, range, range, range)) {
            BlockState state = world.getBlockState(taterPos);
            if (isVibrationTater(state)) {
                taters.add(taterPos.asLong());
            }
        }

        if (taters.isEmpty()) {
            return null;
        }

        int index = world.getRandom().nextInt(taters.size());
        BlockPos taterPos = BlockPos.of(taters.getLong(index));

        return new VibrationParticleOption(new BlockPositionSource(taterPos), (int) Math.floor(Math.sqrt(pos.distSqr(taterPos))));
    }

    private static boolean isVibrationTater(BlockState state) {
        return state.getBlock() instanceof TinyPotatoBlock && !state.is(NEBlockTags.NON_VIBRATING_TATERS);
    }
}
