package xyz.nucleoid.extras.lobby.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InfiniteDropperBlock extends InfiniteDispenserBlock {
    private static final DispenseItemBehavior BEHAVIOR = new DefaultDispenseItemBehavior();

    public InfiniteDropperBlock(Properties settings) {
        super(settings);
    }
    
    @Override
    protected Block getVirtualBlock() {
        return Blocks.DROPPER;
    }

    protected BlockEntityType<? extends DispenserBlockEntity> getBlockEntityType() {
        return NEBlocks.INFINITE_DROPPER_ENTITY;
    }

    @Override
    protected DispenseItemBehavior getDispenseMethod(Level world, ItemStack stack) {
        return BEHAVIOR;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InfiniteDropperBlockEntity(pos, state);
    }
}
