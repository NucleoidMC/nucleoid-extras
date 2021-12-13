package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.block.VirtualBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class InfiniteDispenserBlock extends DispenserBlock implements VirtualBlock {
    public InfiniteDispenserBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void dispense(ServerWorld world, BlockPos pos) {
        BlockPointerImpl pointer = new BlockPointerImpl(world, pos);
        DispenserBlockEntity blockEntity = (DispenserBlockEntity) pointer.getBlockEntity();

        int slot = blockEntity.chooseNonEmptySlot();

        if (slot < 0) {
            world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
            world.emitGameEvent(GameEvent.DISPENSE_FAIL, pos);
        } else {
            ItemStack stack = blockEntity.getStack(slot);
            DispenserBehavior behavior = this.getBehaviorForItem(stack);

            if (behavior != DispenserBehavior.NOOP) {
                behavior.dispense(pointer, stack.copy());
            }
        }
    }

    @Override
    public Block getVirtualBlock() {
        return Blocks.DISPENSER;
    }

    @Override
    public BlockState getVirtualBlockState(BlockState state) {
        return VirtualBlock.super.getVirtualBlockState(state)
            .with(FACING, state.get(FACING))
            .with(TRIGGERED, state.get(TRIGGERED));
    }
}
