package xyz.nucleoid.extras.lobby.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InfiniteDropperBlockEntity extends InfiniteDispenserBlockEntity {
    public InfiniteDropperBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.INFINITE_DROPPER_ENTITY, pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.dropper");
    }
}
