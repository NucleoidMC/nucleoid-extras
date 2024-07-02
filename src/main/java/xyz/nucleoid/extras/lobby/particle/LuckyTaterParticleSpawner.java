package xyz.nucleoid.extras.lobby.particle;

import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class LuckyTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final TaterParticleSpawner INSTANCE = new LuckyTaterParticleSpawner();

    private LuckyTaterParticleSpawner() {
        super();
    }

    @Override
    protected ParticleEffect getParticleEffect(TaterParticleContext context) {
        var random = context.world().getRandom();

        int fromColor = getRandomColor(random);
        int toColor = getRandomColor(random);

        int scale = random.nextInt(3);
        return new DustColorTransitionParticleEffect(Vec3d.unpackRgb(fromColor).toVector3f(), Vec3d.unpackRgb(toColor).toVector3f(), scale);
    }

    private static int getRandomColor(Random random) {
        return random.nextInt() * 0xFFFFFF;
    }
}
