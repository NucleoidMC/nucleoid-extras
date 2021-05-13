package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class LaunchPadBlockEntity extends BlockEntity {
    private float pitch = 10;
    private float power = 4;

    public LaunchPadBlockEntity() {
        super(NEBlocks.LAUNCH_PAD_ENTITY);
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getPower() {
        return this.power;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag main = super.toTag(tag);
        main.putFloat("Pitch", this.pitch);
        main.putFloat("Power", this.power);
        return main;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.pitch = tag.getFloat("Pitch");
        this.power = tag.getFloat("Power");
    }
}
