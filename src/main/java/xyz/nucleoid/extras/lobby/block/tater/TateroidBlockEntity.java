package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    private RegistryEntry<SoundEvent> sound;

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

    public double getParticleSpeed() {
        return this.pitch / 24d;
    }

    private RegistryEntry<SoundEvent> getSound() {
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

        RegistryEntry<SoundEvent> sound = this.getSound();
        if (sound == null) return;

        double x = this.pos.getX() + 0.5;
        double y = this.pos.getY() + 0.9;
        double z = this.pos.getZ() + 0.5;
        this.world.playSound(null, x, y, z, sound, SoundCategory.RECORDS, 3, this.pitch / 24f, this.world.random.nextLong());

        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.NOTE, x, y, z, 0, 1, 0, 0, this.getParticleSpeed());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt(DURATION_KEY, this.duration);
        nbt.putInt(TEMPO_KEY, this.tempo);
        nbt.putInt(PITCH_KEY, this.pitch);

        if (sound != null) {
            Registries.SOUND_EVENT.createEntryCodec().encodeStart(NbtOps.INSTANCE, sound).result()
                    .ifPresent(sound -> nbt.put(SOUND_KEY, sound));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.duration = nbt.getInt(DURATION_KEY);
        this.tempo = nbt.getInt(TEMPO_KEY);
        this.pitch = nbt.getInt(PITCH_KEY);

        Registries.SOUND_EVENT.createEntryCodec().parse(NbtOps.INSTANCE, nbt.get(SOUND_KEY)).result()
            .ifPresent(entry -> this.sound = entry);
    }

    protected static void serverTick(World world, BlockPos pos, BlockState state, TateroidBlockEntity blockEntity) {
        blockEntity.playSound(world.getTime());
    }
}
