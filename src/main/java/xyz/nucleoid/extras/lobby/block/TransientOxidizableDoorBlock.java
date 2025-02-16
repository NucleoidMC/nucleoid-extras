package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.OxidizableDoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class TransientOxidizableDoorBlock extends TransientDoorBlock implements Oxidizable {
    private final Oxidizable.OxidationLevel oxidationLevel;

    public TransientOxidizableDoorBlock(Block block, Block.Settings settings) {
        super(block, settings);
        this.oxidationLevel = block instanceof OxidizableDoorBlock door ? door.getDegradationLevel() : Oxidizable.OxidationLevel.UNAFFECTED;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            this.tickDegradation(state, world, pos, random);
        }
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }
}
