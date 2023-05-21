package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import org.joml.Vector3f;

import java.util.stream.Stream;

public class ColorPatternTaterBlock extends CubicPotatoBlock {
    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterBlock(Settings settings, Vector3f[] pattern, String texture) {
        super(settings, (ParticleEffect) null, texture);

        this.particleEffects = Stream.of(pattern).map(color ->
            new DustParticleEffect(color, 1)
        ).toArray(ParticleEffect[]::new);
    }

    @Override
    public ParticleEffect getParticleEffect(int time) {
        return this.particleEffects[(time / 10) % this.particleEffects.length];
    }
}
