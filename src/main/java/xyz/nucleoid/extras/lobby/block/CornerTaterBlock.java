package xyz.nucleoid.extras.lobby.block;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CornerTaterBlock extends TinyPotatoBlock {
    private static final double VERTICAL_SHRINK = 0.4;

    public CornerTaterBlock(Settings settings, ParticleEffect particleEffect, String texture) {
        super(settings, particleEffect, texture);
    }

    @Override
    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            double minX = pos.getX();
            double minY = pos.getY();
            double minZ = pos.getZ();

            double maxX = minX + 1;
            double maxY = minY + 1;
            double maxZ = minZ + 1;

            // Vertices
            world.spawnParticles(particleEffect, minX, minY, minZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, minX, minY, maxZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, minX, maxY, minZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, minX, maxY, maxZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, maxX, minY, minZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, maxX, minY, maxZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, maxX, maxY, minZ, 0, 0, 0, 0, 0);
            world.spawnParticles(particleEffect, maxX, maxY, maxZ, 0, 0, 0, 0, 0);
        }
    }

    @Override
    public void spawnPlayerParticles(ServerPlayerEntity player) {
        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            Box box = player.getBoundingBox();

            double deltaX = box.getXLength() / 2d;
            double deltaY = (box.getYLength() - VERTICAL_SHRINK) / 2d;
            double deltaZ = box.getZLength() / 2d;

            double minX = player.getX() - deltaX;
            double minY = player.getY();
            double minZ = player.getZ() - deltaZ;

            double halfY = player.getY() + deltaY;

            double maxX = player.getX() + deltaX;
            double maxY = halfY + deltaY;
            double maxZ = player.getZ() + deltaZ;

            // Vertices
            player.getWorld().spawnParticles(particleEffect, minX, minY, minZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, minX, minY, maxZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, minX, maxY, minZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, minX, maxY, maxZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, maxX, minY, minZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, maxX, minY, maxZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, maxX, maxY, minZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, maxX, maxY, maxZ, 0, 0, 0, 0, 0);

            // Intermediate
            player.getWorld().spawnParticles(particleEffect, minX, halfY, minZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, minX, halfY, maxZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, maxX, halfY, minZ, 0, 0, 0, 0, 0);
            player.getWorld().spawnParticles(particleEffect, maxX, halfY, maxZ, 0, 0, 0, 0, 0);
        }
    }
}
