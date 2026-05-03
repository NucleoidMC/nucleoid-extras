package xyz.nucleoid.extras.lobby.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InfiniteDispenserBlockEntity extends DispenserBlockEntity {
    protected InfiniteDispenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public InfiniteDispenserBlockEntity(BlockPos pos, BlockState state) {
        this(NEBlocks.INFINITE_DISPENSER_ENTITY, pos, state);
    }
}
