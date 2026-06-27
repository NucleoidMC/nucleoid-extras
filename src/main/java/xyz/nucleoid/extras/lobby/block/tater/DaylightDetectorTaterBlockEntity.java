package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class DaylightDetectorTaterBlockEntity extends BlockEntity {
	public DaylightDetectorTaterBlockEntity(BlockPos pos, BlockState state) {
		super(NEBlocks.DAYLIGHT_DETECTOR_TATER_ENTITY, pos, state);
	}
}
