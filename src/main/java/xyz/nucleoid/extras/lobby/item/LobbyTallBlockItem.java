package xyz.nucleoid.extras.lobby.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LobbyTallBlockItem extends LobbyBlockItem {
    private static final int UP_FLAGS = Block.FORCE_STATE | Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_ALL;

    public LobbyTallBlockItem(Block block, Settings settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        World world = context.getWorld();

        BlockPos upPos = context.getBlockPos().up();
        BlockState upState = world.isWater(upPos) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();

        world.setBlockState(upPos, upState, UP_FLAGS);

        return super.place(context, state);
    }
}
