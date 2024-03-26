package xyz.nucleoid.extras.lobby.block.tater;

import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public final class CorruptaterBlock extends CubicPotatoBlock {
    private final Random random = new Random();
    public CorruptaterBlock(AbstractBlock.Settings settings, int particleRate) {
        super(settings, ParticleTypes.ENTITY_EFFECT, PolymerUtils.NO_TEXTURE_HEAD_VALUE, particleRate);
    }

    @Override
    public ParticleEffect getParticleEffect(int time) {
        return getTater().getParticleEffect(time);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, ServerPlayerEntity player) {
        var tater = getTater();
        return tater.getPolymerSkinValue(tater.getDefaultState(), pos, player);
    }

    private CubicPotatoBlock getTater() {
        return CubicPotatoBlock.CUBIC_TATERS.get(random.nextInt(CubicPotatoBlock.CUBIC_TATERS.size()));
    }

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey() + "." + random.nextInt(7);
    }
}
