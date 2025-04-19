package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.extras.lobby.NEBlocks;

import java.util.Optional;

public class LaunchPadBlockEntity extends BlockEntity {
    public static final String PITCH_KEY = "Pitch";
    public static final String POWER_KEY = "Power";
    public static final String SOUND_KEY = "sound";

    private float pitch = LauncherComponent.DEFAULT.pitch();
    private float power = LauncherComponent.DEFAULT.power();

    private Optional<RegistryEntry<SoundEvent>> sound = LauncherComponent.DEFAULT.sound();

    public LaunchPadBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.LAUNCH_PAD_ENTITY, pos, state);
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getPower() {
        return this.power;
    }

    public Optional<RegistryEntry<SoundEvent>> getSound() {
        return this.sound;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        nbt.putFloat(PITCH_KEY, this.pitch);
        nbt.putFloat(POWER_KEY, this.power);

        if (this.sound.isPresent()) {
            Codecs.optional(SoundEvent.ENTRY_CODEC)
                    .encodeStart(registries.getOps(NbtOps.INSTANCE), this.sound)
                    .result()
                    .ifPresent(sound -> nbt.put(SOUND_KEY, sound));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.pitch = nbt.getFloat(PITCH_KEY, 0);
        this.power = nbt.getFloat(POWER_KEY, 0);

        if (nbt.contains(SOUND_KEY)) {
            this.sound = nbt.get(SOUND_KEY, SoundEvent.ENTRY_CODEC, registries.getOps(NbtOps.INSTANCE));
        } else {
            this.sound = Optional.empty();
        }
    }
}
