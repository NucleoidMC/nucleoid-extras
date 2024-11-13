package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.extras.lobby.particle.RandomTaterParticleSpawner;
import xyz.nucleoid.packettweaker.PacketContext;

public final class CorruptaterBlock extends CubicPotatoBlock {
    public static final MapCodec<CorruptaterBlock> CODEC = createCodec(CorruptaterBlock::new);

    private final Random random = Random.createLocal();
    public CorruptaterBlock(AbstractBlock.Settings settings) {
        super(settings, new RandomTaterParticleSpawner(CorruptaterBlock::getTater, RandomTaterParticleSpawner.DEFAULT_PLAYER_PARTICLE_RATE, RandomTaterParticleSpawner.DEFAULT_BLOCK_PARTICLE_CHANCE), PolymerUtils.NO_TEXTURE_HEAD_VALUE);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        var player = context.getPlayer();
        var tater = getTater(player == null ? random : player.getRandom());
        return tater.getPolymerSkinValue(tater.getDefaultState(), pos, context);
    }

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey() + "." + random.nextInt(7);
    }

    @Override
    public MapCodec<? extends CorruptaterBlock> getCodec() {
        return CODEC;
    }

    public static CubicPotatoBlock getTater(Random random) {
        return CubicPotatoBlock.CUBIC_TATERS.get(random.nextInt(CubicPotatoBlock.CUBIC_TATERS.size()));
    }
}
