package xyz.nucleoid.extras.lobby.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.extras.lobby.NEBlocks;

import java.util.Optional;

public class LaunchPadBlockEntity extends BlockEntity {
    public static final String PITCH_KEY = "Pitch";
    public static final String POWER_KEY = "Power";
    public static final String SOUND_KEY = "sound";

    private float pitch = LauncherComponent.DEFAULT.pitch();
    private float power = LauncherComponent.DEFAULT.power();

    private Optional<Holder<SoundEvent>> sound = LauncherComponent.DEFAULT.sound();

    public LaunchPadBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.LAUNCH_PAD_ENTITY, pos, state);
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getPower() {
        return this.power;
    }

    public Optional<Holder<SoundEvent>> getSound() {
        return this.sound;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putFloat(PITCH_KEY, this.pitch);
        view.putFloat(POWER_KEY, this.power);

        if (this.sound.isPresent()) {
            view.store(SOUND_KEY, SoundEvent.CODEC, this.sound.get());
        }
    }

    @Override
    public void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.pitch = view.getFloatOr(PITCH_KEY, 0);
        this.power = view.getFloatOr(POWER_KEY, 0);

        this.sound = view.read(SOUND_KEY, SoundEvent.CODEC);
    }
}
