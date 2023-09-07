package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MarkerTaterBlock extends CubicPotatoBlock {
    public MarkerTaterBlock(Settings settings, BlockState particleState, String texture) {
        super(settings, new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, particleState), texture, 12);
    }

    public MarkerTaterBlock(Settings settings, Block particleBlock, String texture) {
        this(settings, particleBlock.getDefaultState(), texture);
    }

    public double getPlayerParticleYOffset() {
        return 0.5;
    }

    @Override
    public void spawnPlayerParticles(ServerPlayerEntity player) {
        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);

        if (particleEffect != null) {
            double x = player.getX();
            double y = player.getY() + this.getPlayerParticleYOffset();
            double z = player.getZ();

            player.getServerWorld().spawnParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.15;
            double z = pos.getZ() + 0.5;

            world.spawnParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
