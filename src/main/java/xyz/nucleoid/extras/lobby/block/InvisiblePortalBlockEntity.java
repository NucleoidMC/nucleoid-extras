package xyz.nucleoid.extras.lobby.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class InvisiblePortalBlockEntity extends BlockEntity {
    public Vec3d target = Vec3d.ZERO;

    public InvisiblePortalBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.INVISIBLE_PORTAL_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.target = Vec3d.CODEC.parse(NbtOps.INSTANCE, nbt.get("target")).get().left().orElse(Vec3d.ZERO);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("target", Vec3d.CODEC.encodeStart(NbtOps.INSTANCE, this.target).get().left().orElse(new NbtList()));
    }
}
