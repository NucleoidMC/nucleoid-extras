package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InfiniteDropperBlockEntity extends InfiniteDispenserBlockEntity {
    public InfiniteDropperBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.INFINITE_DROPPER_ENTITY, pos, state);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.dropper");
    }
}
