package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

public class DaylightDetectorTaterBlock extends CubicPotatoBlock implements EntityBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public final boolean inverted;

    public DaylightDetectorTaterBlock(Properties settings, String texture, boolean inverted) {
        super(settings, Blocks.DAYLIGHT_DETECTOR.defaultBlockState().setValue(BlockStateProperties.INVERTED, inverted), texture);
        this.inverted = inverted;
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
    }

    private static void updateState(BlockState state, Level world, BlockPos pos) {
        int power = world.getBrightness(LightLayer.SKY, pos) - world.getSkyDarken();
        var skyAngle = world.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, pos) * ((float) Math.PI / 180F);
        boolean inverted = ((DaylightDetectorTaterBlock) state.getBlock()).inverted;
        if (inverted) {
            power = 15 - power;
        } else if (power > 0) {
            float g = skyAngle < (float) Math.PI ? 0.0f : (float) Math.PI * 2;
            skyAngle += (g - skyAngle) * 0.2f;
            power = Math.round((float) power * Mth.cos(skyAngle));
        }
        power = Mth.clamp(power, 0, 15);
        if (state.getValue(POWER) != power) {
            world.setBlock(pos, state.setValue(POWER, power), Block.UPDATE_ALL);
        }
    }

    private static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T blockEntity) {
        if (world.getGameTime() % 20L == 0L) {
            DaylightDetectorTaterBlock.updateState(state, world, pos);
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DaylightDetectorTaterBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClientSide() && world.dimensionType().hasSkyLight()) {
            return BlockWithEntityAccessor.validateTicker(type, NEBlocks.DAYLIGHT_DETECTOR_TATER_ENTITY, DaylightDetectorTaterBlock::tick);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER);
    }
}
