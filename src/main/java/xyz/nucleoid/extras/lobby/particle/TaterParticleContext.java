package xyz.nucleoid.extras.lobby.particle;

import java.util.Optional;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public sealed interface TaterParticleContext {
    ServerWorld world();

    Vec3d getPos();

    Vec3d getEyePos();

    Box getBox();

    <T extends BlockEntity> Optional<T> getBlockEntity(BlockEntityType<T> type);

    default int getTime() {
        return this.world().getServer().getTicks();
    }

    record Player(ServerPlayerEntity player) implements TaterParticleContext {
        @Override
        public ServerWorld world() {
            return this.player.getServerWorld();
        }

        @Override
        public Vec3d getPos() {
            return this.player.getPos();
        }

        @Override
        public Vec3d getEyePos() {
            return new Vec3d(player.getX(), player.getEyeY() - 0.2, player.getZ());
        }

        @Override
        public Box getBox() {
            return this.player.getBoundingBox();
        }

        @Override
        public <T extends BlockEntity> Optional<T> getBlockEntity(BlockEntityType<T> type) {
            return Optional.empty();
        }
    }

    record Block(BlockPos blockPos, ServerWorld world) implements TaterParticleContext {
        @Override
        public Vec3d getPos() {
            return this.blockPos.toCenterPos();
        }

        @Override
        public Vec3d getEyePos() {
            return Vec3d.ofCenter(this.blockPos, 0.25);
        }

        @Override
        public Box getBox() {
            return new Box(this.blockPos);
        }

        @Override
        public <T extends BlockEntity> Optional<T> getBlockEntity(BlockEntityType<T> type) {
            return this.world.getBlockEntity(this.blockPos, type);
        }
    }
}
