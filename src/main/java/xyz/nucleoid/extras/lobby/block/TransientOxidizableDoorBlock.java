package xyz.nucleoid.extras.lobby.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class TransientOxidizableDoorBlock extends TransientDoorBlock implements WeatheringCopper {
    private final WeatheringCopper.WeatherState oxidationLevel;

    public TransientOxidizableDoorBlock(Block block, Block.Settings settings) {
        super(block, settings);
        this.oxidationLevel = block instanceof WeatheringCopperDoorBlock door ? door.getAge() : WeatheringCopper.WeatherState.UNAFFECTED;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            this.changeOverTime(state, world, pos, random);
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatherState getAge() {
        return this.oxidationLevel;
    }
}
