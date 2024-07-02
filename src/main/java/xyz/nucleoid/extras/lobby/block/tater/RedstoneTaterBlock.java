package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawner;

public class RedstoneTaterBlock extends CubicPotatoBlock {
	public RedstoneTaterBlock(Settings settings, TaterParticleSpawner particleSpawner, String texture) {
		super(settings, particleSpawner, texture);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 15;
	}
}
