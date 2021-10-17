package xyz.nucleoid.extras.lobby.block;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

public class TateroidBlock extends TinyPotatoBlock implements BlockEntityProvider {
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final int FULL_DURATION = 15 * SharedConstants.TICKS_PER_SECOND;

    private final SoundEvent defaultSound;

    public TateroidBlock(Settings settings, SoundEvent defaultSound, String texture) {
        super(settings, ParticleTypes.NOTE, texture);
        this.defaultSound = defaultSound;

        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    private void activate(World world, BlockPos pos, int duration) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            var blockEntity = optional.get();
            blockEntity.setDuration(duration);
        }
    }

    public SoundEvent getDefaultSound() {
        return this.defaultSound;
    }

    private int getDurationFromPower(int power) {
        if (power == 1) {
            return -1;
        }

        return (int) (FULL_DURATION * (power / (float) Properties.LEVEL_15_MAX));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result.isAccepted() && !world.isClient()) {
            this.activate(world, pos, FULL_DURATION);
        }

        return result;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient()) {
            int power = world.getReceivedRedstonePower(pos);
            boolean powered = power > 0;

            if (powered != state.get(POWERED)) {
                if (powered) {
                    this.activate(world, pos, this.getDurationFromPower(power));
                }

                world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            int duration = optional.get().getDuration();
            float power = (duration / (float) FULL_DURATION) * Properties.LEVEL_15_MAX;

            return (int) MathHelper.clamp(power, 0, Properties.LEVEL_15_MAX);
        }

        return 0;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TateroidBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : BlockWithEntityAccessor.checkType(type, NEBlocks.TATEROID_ENTITY, TateroidBlockEntity::serverTick);
    }
}
