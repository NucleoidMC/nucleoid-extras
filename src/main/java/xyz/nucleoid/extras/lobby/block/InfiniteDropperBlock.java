package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InfiniteDropperBlock extends InfiniteDispenserBlock {
    private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

    public InfiniteDropperBlock(Settings settings) {
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
    protected DispenserBehavior getBehaviorForItem(World world, ItemStack stack) {
        return BEHAVIOR;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InfiniteDropperBlockEntity(pos, state);
    }
}
