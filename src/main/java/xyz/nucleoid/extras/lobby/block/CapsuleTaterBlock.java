package xyz.nucleoid.extras.lobby.block;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

public class CapsuleTaterBlock extends ColorTaterBlock implements LuckyTaterDrop {
    private static final int PARTICLE_COUNT = 8;

    private static final double BLOCK_PARTICLE_RADIUS = 0.8;
    private static final double PLAYER_PARTICLE_RADIUS = 0.5;

    private final int weight;

    public CapsuleTaterBlock(Settings settings, Vector3f color, int weight, String texture) {
        super(settings, color, texture);

        this.weight = weight;
    }

    @Override
    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            this.spawnParticlesAround(world, particleEffect, pos.getX() + 0.5, BLOCK_PARTICLE_RADIUS, pos.getY() + 0.5, pos.getZ() + 0.5, BLOCK_PARTICLE_RADIUS, 0);
        }
    }

    @Override
    public void spawnPlayerParticles(ServerPlayerEntity player) {
        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            double radius = player.getWidth() / 2 + PLAYER_PARTICLE_RADIUS;
            double y = player.getBodyY(0.5);
            double centerAngle = player.getYaw() * Math.PI / 180;

            this.spawnParticlesAround(player.getWorld(), particleEffect, player.getX(), radius, y, player.getZ(), radius, centerAngle);
        }
    }

    private void spawnParticlesAround(ServerWorld world, ParticleEffect particleEffect, double centerX, double radiusX, double y, double centerZ, double radiusZ, double centerAngle) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = i / (double) PARTICLE_COUNT * Math.PI * 2 + centerAngle;

            double x = centerX + Math.cos(angle) * radiusX;
            double z = centerZ + Math.sin(angle) * radiusZ;

            world.spawnParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean isFickle() {
        return true;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }
}
