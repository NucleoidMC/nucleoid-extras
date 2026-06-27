package xyz.nucleoid.extras.lobby.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LobbyTallBlockItem extends LobbyBlockItem {
    private static final int UP_FLAGS = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_IMMEDIATE | Block.UPDATE_ALL;

    public LobbyTallBlockItem(Block block, Properties settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        Level world = context.getLevel();

        BlockPos upPos = context.getClickedPos().above();
        BlockState upState = world.isWaterAt(upPos) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();

        world.setBlock(upPos, upState, UP_FLAGS);

        return super.placeBlock(context, state);
    }
}
