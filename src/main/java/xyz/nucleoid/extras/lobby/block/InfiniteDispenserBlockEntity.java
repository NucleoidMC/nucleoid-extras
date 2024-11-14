package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InfiniteDispenserBlockEntity extends DispenserBlockEntity {
    protected InfiniteDispenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public InfiniteDispenserBlockEntity(BlockPos pos, BlockState state) {
        this(NEBlocks.INFINITE_DISPENSER_ENTITY, pos, state);
    }
}
