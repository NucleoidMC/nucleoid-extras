package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneTaterBlock extends CubicPotatoBlock {
	public RedstoneTaterBlock(Properties settings, ParticleOptions particleEffect, String texture) {
		super(settings, particleEffect, texture);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return 15;
	}
}
