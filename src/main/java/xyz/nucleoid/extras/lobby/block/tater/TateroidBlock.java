package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

public class TateroidBlock extends CubicPotatoBlock implements BlockEntityProvider {
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final int FULL_DURATION = 15 * SharedConstants.TICKS_PER_SECOND;

    private final RegistryEntry<SoundEvent> defaultSound;
    private final double particleColor;

    public TateroidBlock(Settings settings, RegistryEntry<SoundEvent> defaultSound, double particleColor, String texture) {
        super(settings, ParticleTypes.NOTE, texture);

        this.defaultSound = defaultSound;
        this.particleColor = particleColor;

        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    private void activate(World world, BlockPos pos, int duration) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            var blockEntity = optional.get();
            blockEntity.setDuration(duration);
        }
    }

    public RegistryEntry<SoundEvent> getDefaultSound() {
        return this.defaultSound;
    }

    private int getDurationFromPower(int power) {
        if (power == 1) {
            return -1;
        }

        return (int) (FULL_DURATION * (power / (float) Properties.LEVEL_15_MAX));
    }

    @Override
    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY).ifPresent(blockEntity -> {
                world.spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 1, 0, 0, blockEntity.getParticleSpeed());
            });
        }
    }

    @Override
    public void spawnPlayerParticles(ServerPlayerEntity player) {
        if (this.particleColor == -1) {
            super.spawnPlayerParticles(player);
            return;
        }

        Box box = player.getBoundingBox();

        double deltaX = box.getXLength() / 2d;
        double deltaY = box.getYLength() / 2d;
        double deltaZ = box.getZLength() / 2d;

        double x = player.getX() + (player.getRandom().nextGaussian() * deltaX);
        double y = player.getY() + (player.getRandom().nextGaussian() * deltaY);
        double z = player.getZ() + (player.getRandom().nextGaussian() * deltaZ);

        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.getServerWorld().spawnParticles(particleEffect, x, y, z, 0, 1, 0, 0, this.particleColor);
        }
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
