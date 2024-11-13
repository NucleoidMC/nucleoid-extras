package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.particle.TateroidParticleSpawner;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

public class TateroidBlock extends CubicPotatoBlock implements BlockEntityProvider {
    public static final MapCodec<TateroidBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                createSettingsCodec(),
                SoundEvent.ENTRY_CODEC.fieldOf("default_sound").forGetter(TateroidBlock::getDefaultSound),
                TateroidParticleSpawner.DEFAULT_PARTICLE_COLOR_CODEC.forGetter(tater -> tater.defaultParticleColor),
                Codec.STRING.fieldOf("texture").forGetter(TateroidBlock::getItemTexture)
        ).apply(instance, TateroidBlock::new)
    );

    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final int FULL_DURATION = 15 * SharedConstants.TICKS_PER_SECOND;

    private final RegistryEntry<SoundEvent> defaultSound;
    private final double defaultParticleColor;

    public TateroidBlock(Settings settings, RegistryEntry<SoundEvent> defaultSound, double defaultParticleColor, String texture) {
        super(settings, new TateroidParticleSpawner(ParticleTypes.NOTE, defaultParticleColor), texture);

        this.defaultSound = defaultSound;
        this.defaultParticleColor = defaultParticleColor;

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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hit);
        if (result.isAccepted() && !world.isClient()) {
            this.activate(world, pos, FULL_DURATION);
        }

        return result;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, WireOrientation wireOrientation, boolean notify) {
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
    public MapCodec<? extends TateroidBlock> getCodec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : BlockWithEntityAccessor.validateTicker(type, NEBlocks.TATEROID_ENTITY, TateroidBlockEntity::serverTick);
    }
}
