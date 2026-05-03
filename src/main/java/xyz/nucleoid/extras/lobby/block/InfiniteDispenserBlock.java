package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.packettweaker.PacketContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfiniteDispenserBlock extends DispenserBlock implements PolymerBlock {
    private static final Logger LOGGER = LogManager.getLogger(InfiniteDispenserBlock.class);

    public InfiniteDispenserBlock(Properties settings) {
        super(settings);
    }

    protected Block getVirtualBlock() {
        return Blocks.DISPENSER;
    }

    protected BlockEntityType<? extends DispenserBlockEntity> getBlockEntityType() {
        return NEBlocks.INFINITE_DISPENSER_ENTITY;
    }

    @Override
    protected void dispenseFrom(ServerLevel world, BlockState state, BlockPos pos) {
        DispenserBlockEntity blockEntity = world.getBlockEntity(pos, this.getBlockEntityType()).orElse(null);

        if (blockEntity == null) {
            LOGGER.warn("Ignoring dispensing attempt for " + this.getName().getString() + " without matching block entity at {}", pos);
        } else {
            BlockSource pointer = new BlockSource(world, pos, state, blockEntity);

            int slot = blockEntity.getRandomSlot(world.getRandom());

            if (slot < 0) {
                world.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, pos, 0);
                world.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(blockEntity.getBlockState()));
            } else {
                ItemStack stack = blockEntity.getItem(slot);
                DispenseItemBehavior behavior = this.getDispenseMethod(world, stack);

                if (behavior != DispenseItemBehavior.NOOP) {
                    behavior.dispense(pointer, stack.copy());
                }
            }
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return this.getVirtualBlock().withPropertiesOf(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfiniteDispenserBlockEntity(pos, state);
    }
}
