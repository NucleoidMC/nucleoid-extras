package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class LaunchPadBlockEntity extends BlockEntity {
    public static final String PITCH_KEY = "Pitch";
    public static final String POWER_KEY = "Power";

    public static final float DEFAULT_PITCH = 10;
    public static final float DEFAULT_POWER = 4;

    private float pitch = DEFAULT_PITCH;
    private float power = DEFAULT_POWER;

    public LaunchPadBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.LAUNCH_PAD_ENTITY, pos, state);
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getPower() {
        return this.power;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putFloat(PITCH_KEY, this.pitch);
        nbt.putFloat(POWER_KEY, this.power);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.pitch = nbt.getFloat(PITCH_KEY);
        this.power = nbt.getFloat(POWER_KEY);
    }
}
