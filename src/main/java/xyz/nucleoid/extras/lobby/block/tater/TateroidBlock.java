package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

public class TateroidBlock extends CubicPotatoBlock implements EntityBlock {
    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int FULL_DURATION = 15 * SharedConstants.TICKS_PER_SECOND;

    private final Holder<SoundEvent> defaultSound;
    private final double particleColor;

    public TateroidBlock(Properties settings, Holder<SoundEvent> defaultSound, double particleColor, String texture) {
        super(settings, ParticleTypes.NOTE, texture);

        this.defaultSound = defaultSound;
        this.particleColor = particleColor;

        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    private void activate(Level world, BlockPos pos, int duration) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            var blockEntity = optional.get();
            blockEntity.setDuration(duration);
        }
    }

    public Holder<SoundEvent> getDefaultSound() {
        return this.defaultSound;
    }

    private int getDurationFromPower(int power) {
        if (power == 1) {
            return -1;
        }

        return (int) (FULL_DURATION * (power / (float) BlockStateProperties.MAX_LEVEL_15));
    }

    @Override
    public void spawnBlockParticles(ServerLevel world, BlockPos pos, ParticleOptions particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY).ifPresent(blockEntity -> {
                world.sendParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 1, 0, 0, blockEntity.getParticleSpeed());
            });
        }
    }

    @Override
    public void spawnPlayerParticles(ServerPlayer player) {
        if (this.particleColor == -1) {
            super.spawnPlayerParticles(player);
            return;
        }

        AABB box = player.getBoundingBox();

        double deltaX = box.getXsize() / 2d;
        double deltaY = box.getYsize() / 2d;
        double deltaZ = box.getZsize() / 2d;

        double x = player.getX() + (player.getRandom().nextGaussian() * deltaX);
        double y = player.getY() + (player.getRandom().nextGaussian() * deltaY);
        double z = player.getZ() + (player.getRandom().nextGaussian() * deltaZ);

        ParticleOptions particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.level().sendParticles(particleEffect, x, y, z, 0, 1, 0, 0, this.particleColor);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, world, pos, player, hit);
        if (result.consumesAction() && !world.isClientSide()) {
            this.activate(world, pos, FULL_DURATION);
        }

        return result;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, Orientation wireOrientation, boolean notify) {
        if (!world.isClientSide()) {
            int power = world.getBestNeighborSignal(pos);
            boolean powered = power > 0;

            if (powered != state.getValue(POWERED)) {
                if (powered) {
                    this.activate(world, pos, this.getDurationFromPower(power));
                }

                world.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            int duration = optional.get().getDuration();
            float power = (duration / (float) FULL_DURATION) * BlockStateProperties.MAX_LEVEL_15;

            return (int) Mth.clamp(power, 0, BlockStateProperties.MAX_LEVEL_15);
        }

        return 0;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TateroidBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : BlockWithEntityAccessor.validateTicker(type, NEBlocks.TATEROID_ENTITY, TateroidBlockEntity::serverTick);
    }
}
