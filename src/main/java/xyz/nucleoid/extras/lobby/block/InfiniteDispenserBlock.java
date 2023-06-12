package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
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

public class InfiniteDispenserBlock extends DispenserBlock implements PolymerBlock {
    public InfiniteDispenserBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void dispense(ServerWorld world, BlockPos pos) {
        BlockPointerImpl pointer = new BlockPointerImpl(world, pos);
        DispenserBlockEntity blockEntity = pointer.getBlockEntity();

        int slot = blockEntity.chooseNonEmptySlot(world.getRandom());

        if (slot < 0) {
            world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
            world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(pointer.getBlockState()));
        } else {
            ItemStack stack = blockEntity.getStack(slot);
            DispenserBehavior behavior = this.getBehaviorForItem(stack);

            if (behavior != DispenserBehavior.NOOP) {
                behavior.dispense(pointer, stack.copy());
            }
        }
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.DISPENSER;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return PolymerBlock.super.getPolymerBlockState(state)
            .with(FACING, state.get(FACING))
            .with(TRIGGERED, state.get(TRIGGERED));
    }
}
