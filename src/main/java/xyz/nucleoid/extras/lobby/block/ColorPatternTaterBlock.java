package xyz.nucleoid.extras.lobby.block;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3f;

import java.util.stream.Stream;

public class ColorPatternTaterBlock extends TinyPotatoBlock {
    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterBlock(Settings settings, Vec3f[] pattern, String texture) {
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
