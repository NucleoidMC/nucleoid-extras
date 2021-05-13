package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.block.VirtualBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaunchPadBlock extends Block implements BlockEntityProvider, VirtualBlock {
    private final Block virtualBlock;

    public LaunchPadBlock(Settings settings, Block virtualBlock) {
        super(settings);
        this.virtualBlock = virtualBlock;
    }

    @Override
    public Block getVirtualBlock() {
        return virtualBlock;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof LaunchPadBlockEntity) {
            LaunchPadBlockEntity launchPad = (LaunchPadBlockEntity) blockEntity;

            entity.setVelocity(getVector(launchPad.getPitch(), entity.getYaw(0)).multiply(launchPad.getPower()));
            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
            }
            super.onEntityCollision(state, world, pos, entity);
        }
    }

    private static Vec3d getVector(float pitch, float yaw) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double horizontal = Math.cos(pitchRad);
        return new Vec3d(
                Math.cos(yawRad) * horizontal,
                Math.sin(pitchRad),
                Math.sin(yawRad) * horizontal
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new LaunchPadBlockEntity();
    }
}
