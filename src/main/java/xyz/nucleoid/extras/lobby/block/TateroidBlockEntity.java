package xyz.nucleoid.extras.lobby.block;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class TateroidBlockEntity extends BlockEntity {
    private static final String DURATION_KEY = "Duration";
    private static final String TEMPO_KEY = "Tempo";
    private static final String PITCH_KEY = "Pitch";
    private static final String SOUND_KEY = "Sound";

    private int duration = 0;
    private int tempo = 1 * SharedConstants.TICKS_PER_SECOND;
    private int pitch = 0;
    private SoundEvent sound;

    public TateroidBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.TATEROID_ENTITY, pos, state);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration, boolean markDirty) {
        this.duration = duration;
        if (markDirty) {
            this.markDirty();
        }
    }

    public void setDuration(int duration) {
        this.setDuration(duration, true);
    }

    private SoundEvent getSound() {
        if (this.sound != null) {
            return this.sound;
        }

        Block block = this.getCachedState().getBlock();
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

        SoundEvent sound = this.getSound();
        if (sound == null) return;

        this.world.playSound(null, this.pos, sound, SoundCategory.RECORDS, 3, this.pitch / 24f);

        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.NOTE, this.pos.getX() + 0.5, this.pos.getY() + 0.9, this.pos.getZ() + 0.5, 0, 1, 0, 0, (double) this.pitch / 24d);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt(DURATION_KEY, this.duration);
        nbt.putInt(TEMPO_KEY, this.tempo);
        nbt.putInt(PITCH_KEY, this.pitch);

        if (sound != null) {
            nbt.putString(SOUND_KEY, sound.getId().toString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.duration = nbt.getInt(DURATION_KEY);
        this.tempo = nbt.getInt(TEMPO_KEY);
        this.pitch = nbt.getInt(PITCH_KEY);

        Identifier soundId = Identifier.tryParse(nbt.getString(SOUND_KEY));
        if (soundId != null) {
            this.sound = Registry.SOUND_EVENT.get(soundId);
        }
    }

    protected static void serverTick(World world, BlockPos pos, BlockState state, TateroidBlockEntity blockEntity) {
        blockEntity.playSound(world.getTime());
    }
}
