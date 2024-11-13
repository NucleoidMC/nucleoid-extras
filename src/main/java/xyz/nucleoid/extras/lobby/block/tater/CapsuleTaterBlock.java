package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.collection.Weight;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawner;

public class CapsuleTaterBlock extends CubicPotatoBlock implements LuckyTaterDrop {
    public static final MapCodec<CapsuleTaterBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                createSettingsCodec(),
                TaterParticleSpawner.CODEC.fieldOf("particle_spawner").forGetter(CapsuleTaterBlock::getParticleSpawner),
                Weight.CODEC.fieldOf("weight").forGetter(tater -> tater.weight),
                Codec.STRING.fieldOf("texture").forGetter(CapsuleTaterBlock::getItemTexture)
        ).apply(instance, CapsuleTaterBlock::new)
    );

    private final Weight weight;

    public CapsuleTaterBlock(AbstractBlock.Settings settings, TaterParticleSpawner particleSpawner, Weight weight, String texture) {
        super(settings, particleSpawner, texture);

        this.weight = weight;
    }

    public CapsuleTaterBlock(AbstractBlock.Settings settings, TaterParticleSpawner particleSpawner, int weight, String texture) {
        this(settings, particleSpawner, Weight.of(weight), texture);
    }

    @Override
    public boolean isFickle() {
        return true;
    }

    @Override
    public int getWeight() {
        return this.weight.getValue();
    }

    @Override
    public MapCodec<? extends CapsuleTaterBlock> getCodec() {
        return CODEC;
    }
}
