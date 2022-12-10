package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public final class CorruptaterBlock extends TinyPotatoBlock {
    private final Random random = new Random();
    public CorruptaterBlock(Settings settings, int particleRate) {
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

    private TinyPotatoBlock getTater() {
        return TinyPotatoBlock.TATERS.get(random.nextInt(TinyPotatoBlock.TATERS.size()));
    }

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey() + "." + random.nextInt(7);
    }
}
