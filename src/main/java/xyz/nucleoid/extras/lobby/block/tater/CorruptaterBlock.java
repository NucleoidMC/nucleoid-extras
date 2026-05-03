package xyz.nucleoid.extras.lobby.block.tater;

import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public final class CorruptaterBlock extends CubicPotatoBlock {
    private final Random random = new Random();
    public CorruptaterBlock(BlockBehaviour.Properties settings, int particleRate) {
        super(settings, null, PolymerUtils.NO_TEXTURE_HEAD_VALUE, particleRate);
    }

    @Override
    public ParticleOptions getParticleEffect(int time) {
        return getTater().getParticleEffect(time);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        var tater = getTater();
        return tater.getPolymerSkinValue(tater.defaultBlockState(), pos, context);
    }

    private CubicPotatoBlock getTater() {
        return CubicPotatoBlock.CUBIC_TATERS.get(random.nextInt(CubicPotatoBlock.CUBIC_TATERS.size()));
    }

    @Override
    public String getDescriptionId() {
        return super.getDescriptionId() + "." + random.nextInt(7);
    }
}
