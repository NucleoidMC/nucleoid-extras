package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import xyz.nucleoid.packettweaker.PacketContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfiniteDispenserBlock extends DispenserBlock implements PolymerBlock {
    private static final Logger LOGGER = LogManager.getLogger(InfiniteDispenserBlock.class);

    public InfiniteDispenserBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
        DispenserBlockEntity blockEntity = world.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);

        if (blockEntity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Infinite Dispenser without matching block entity at {}", pos);
        } else {
            BlockPointer pointer = new BlockPointer(world, pos, state, blockEntity);

            int slot = blockEntity.chooseNonEmptySlot(world.getRandom());

            if (slot < 0) {
                world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
                world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(blockEntity.getCachedState()));
            } else {
                ItemStack stack = blockEntity.getStack(slot);
                DispenserBehavior behavior = this.getBehaviorForItem(world, stack);

                if (behavior != DispenserBehavior.NOOP) {
                    behavior.dispense(pointer, stack.copy());
                }
            }
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.DISPENSER.getDefaultState()
            .with(FACING, state.get(FACING))
            .with(TRIGGERED, state.get(TRIGGERED));
    }
}
