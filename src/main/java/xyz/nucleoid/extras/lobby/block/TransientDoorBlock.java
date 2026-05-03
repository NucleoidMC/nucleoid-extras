package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;

public class TransientDoorBlock extends DoorBlock implements PolymerBlock {
    private static final int CLOSE_DELAY = SharedConstants.TICKS_PER_SECOND * 10;

    private static final double RECHECK_RANGE = 8;
    private static final int RECHECK_DELAY = SharedConstants.TICKS_PER_SECOND * 1;

    private final Block polymerBlock;

    public TransientDoorBlock(Block block, Block.Properties settings) {
        super(block instanceof DoorBlock door ? door.type() : BlockSetType.OAK, settings);
        this.polymerBlock = block;
    }

    private void scheduleClose(Level world, BlockPos pos, boolean recheck) {
        world.scheduleTick(pos, this, recheck ? RECHECK_DELAY : CLOSE_DELAY);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && state.getValue(OPEN) && (!oldState.is(this) || !oldState.getValue(OPEN))) {
            this.scheduleClose(world, pos, false);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        var box = new AABB(pos).inflate(RECHECK_RANGE);
        var entities = world.getEntitiesOfClass(Player.class, box);

        if (entities.isEmpty()) {
            this.setOpen(null, world, state, pos, false);
        } else {
            this.scheduleClose(world, pos, true);
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return this.polymerBlock.withPropertiesOf(state);
    }
}
