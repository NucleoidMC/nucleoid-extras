package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MarkerTaterBlock extends CubicPotatoBlock {
    public MarkerTaterBlock(Properties settings, BlockState particleState, String texture) {
        super(settings, new BlockParticleOption(ParticleTypes.BLOCK_MARKER, particleState), texture, 12);
    }

    public MarkerTaterBlock(Properties settings, Block particleBlock, String texture) {
        this(settings, particleBlock.defaultBlockState(), texture);
    }

    public double getPlayerParticleYOffset() {
        return 0.5;
    }

    @Override
    public void spawnPlayerParticles(ServerPlayer player) {
        ParticleOptions particleEffect = this.getPlayerParticleEffect(player);

        if (particleEffect != null) {
            double x = player.getX();
            double y = player.getY() + this.getPlayerParticleYOffset();
            double z = player.getZ();

            player.level().sendParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void spawnBlockParticles(ServerLevel world, BlockPos pos, ParticleOptions particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.15;
            double z = pos.getZ() + 0.5;

            world.sendParticles(particleEffect, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
