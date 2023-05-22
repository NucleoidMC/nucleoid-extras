package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class InfiniteDropperBlock extends InfiniteDispenserBlock {
    private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

    public InfiniteDropperBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
        return BEHAVIOR;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DropperBlockEntity(pos, state);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.DROPPER;
    }
}
