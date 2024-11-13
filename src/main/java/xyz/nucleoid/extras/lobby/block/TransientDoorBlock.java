package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class TransientDoorBlock extends DoorBlock implements PolymerBlock {
    private static final int CLOSE_DELAY = SharedConstants.TICKS_PER_SECOND * 10;

    private static final double RECHECK_RANGE = 8;
    private static final int RECHECK_DELAY = SharedConstants.TICKS_PER_SECOND * 1;

    private final Block polymerBlock;

    public TransientDoorBlock(Block block, Block.Settings settings) {
        super(block instanceof DoorBlock door ? door.getBlockSetType() : BlockSetType.OAK, settings);
        this.polymerBlock = block;
    }

    private void scheduleClose(World world, BlockPos pos, boolean recheck) {
        world.scheduleBlockTick(pos, this, recheck ? RECHECK_DELAY : CLOSE_DELAY);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER && state.get(OPEN) && (!oldState.isOf(this) || !oldState.get(OPEN))) {
            this.scheduleClose(world, pos, false);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var box = new Box(pos).expand(RECHECK_RANGE);
        var entities = world.getNonSpectatingEntities(PlayerEntity.class, box);

        if (entities.isEmpty()) {
            this.setOpen(null, world, state, pos, false);
        } else {
            this.scheduleClose(world, pos, true);
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return this.polymerBlock.getStateWithProperties(state);
    }
}
