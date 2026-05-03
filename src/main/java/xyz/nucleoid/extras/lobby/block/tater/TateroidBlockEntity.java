package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class TateroidBlockEntity extends BlockEntity {
    private static final String DURATION_KEY = "Duration";
    private static final String TEMPO_KEY = "Tempo";
    private static final String PITCH_KEY = "Pitch";
    private static final String SOUND_KEY = "Sound";

    private int duration = 0;
    private int tempo = SharedConstants.TICKS_PER_SECOND;
    private int pitch = 0;
    @Nullable
    private Holder<SoundEvent> sound;

    public TateroidBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.TATEROID_ENTITY, pos, state);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration, boolean markDirty) {
        this.duration = duration;
        if (markDirty) {
            this.setChanged();
        }
    }

    public void setDuration(int duration) {
        this.setDuration(duration, true);
    }

    public double getParticleSpeed() {
        return this.pitch / 24d;
    }

    private Holder<SoundEvent> getSound() {
        if (this.sound != null) {
            return this.sound;
        }

        Block block = this.getBlockState().getBlock();
        if (block instanceof TateroidBlock) {
            return ((TateroidBlock) block).getDefaultSound();
        }

        return null;
    }

    public void playSound(long time) {
        if (this.duration == 0) {
            return;
        } else if (this.duration < 0) {
            this.setDuration(0);
        } else {
            this.setDuration(this.duration - 1, this.duration % 20 == 0);

            if (this.tempo != 0 && time % this.tempo != 0) {
                return;
            }
        }

        Holder<SoundEvent> sound = this.getSound();
        if (sound == null) return;

        double x = this.worldPosition.getX() + 0.5;
        double y = this.worldPosition.getY() + 0.9;
        double z = this.worldPosition.getZ() + 0.5;
        this.level.playSeededSound(null, x, y, z, sound, SoundSource.RECORDS, 3, this.pitch / 24f, this.level.getRandom().nextLong());

        if (this.level instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(ParticleTypes.NOTE, x, y, z, 0, 1, 0, 0, this.getParticleSpeed());
        }
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt(DURATION_KEY, this.duration);
        view.putInt(TEMPO_KEY, this.tempo);
        view.putInt(PITCH_KEY, this.pitch);

        if (sound != null) {
            view.store(SOUND_KEY, SoundEvent.CODEC, sound);
        }
    }

    @Override
    public void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        this.duration = view.getIntOr(DURATION_KEY, 0);
        this.tempo = view.getIntOr(TEMPO_KEY, 0);
        this.pitch = view.getIntOr(PITCH_KEY, 0);

        this.sound = view.read(SOUND_KEY, SoundEvent.CODEC).orElse(null);
    }

    protected static void serverTick(Level world, BlockPos pos, BlockState state, TateroidBlockEntity blockEntity) {
        blockEntity.playSound(world.getGameTime());
    }
}
