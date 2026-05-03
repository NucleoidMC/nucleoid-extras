package xyz.nucleoid.extras.lobby.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xyz.nucleoid.packettweaker.PacketContext;

public class SnakeBlock extends DirectionalBlock implements PolymerBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private final BlockState virtualBlockState;
    private final int delay;
    private final int length;

    public SnakeBlock(Properties settings, BlockState virtualBlockState, int delay, int length) {
        super(settings);

        this.virtualBlockState = virtualBlockState;
        this.delay = delay;
        this.length = length;

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(ACTIVE, false));
    }

    private boolean isActive(BlockState state) {
        return state.getValue(ACTIVE);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return this.isActive(state) ? this.virtualBlockState : Blocks.BROWN_MUSHROOM.defaultBlockState();
    }
    
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return this.isActive(state) ? 15 : 0;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.isActive(state) ? Shapes.block() : Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, Orientation wireOrientation, boolean notify) {
        boolean powered = world.hasNeighborSignal(pos);
        boolean active = this.isActive(state);
        if (powered && !active) {
            this.scheduleTick(world, pos, 1);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (this.isActive(state)) {
            world.setBlockAndUpdate(pos, state.setValue(ACTIVE, false));
        } else {
            world.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));

            this.scheduleTick(world, pos.relative(state.getValue(FACING)), 1);
            this.scheduleTick(world, pos, this.length);
        }
    }

    private void scheduleTick(Level world, BlockPos pos, int multiplier) {
        world.scheduleTick(pos, this, this.delay * multiplier);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(ACTIVE);
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return null;
    }
}
