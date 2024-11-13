package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;

public class LuckyTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final LuckyTaterParticleSpawner INSTANCE = new LuckyTaterParticleSpawner();

    public static final MapCodec<LuckyTaterParticleSpawner> CODEC = MapCodec.unit(INSTANCE);

    private LuckyTaterParticleSpawner() {
        super();
    }

    @Override
    protected ParticleEffect getParticleEffect(TaterParticleContext context) {
        var random = context.world().getRandom();

        int fromColor = getRandomColor(random);
        int toColor = getRandomColor(random);

        int scale = random.nextInt(3);
        return new DustColorTransitionParticleEffect(fromColor, toColor, scale);
    }

    @Override
    public MapCodec<? extends LuckyTaterParticleSpawner> getCodec() {
        return CODEC;
    }

    private static int getRandomColor(Random random) {
        return random.nextInt() * 0xFFFFFF;
    }
}
