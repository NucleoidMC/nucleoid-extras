package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class RedstoneTaterBlock extends CubicPotatoBlock {
	public RedstoneTaterBlock(Settings settings, ParticleEffect particleEffect, String texture) {
		super(settings, particleEffect, texture);
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
