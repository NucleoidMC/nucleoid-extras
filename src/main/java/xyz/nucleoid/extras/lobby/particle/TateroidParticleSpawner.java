package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.extras.lobby.NEBlocks;

public class TateroidParticleSpawner extends SimpleTaterParticleSpawner {
    public static final MapCodec<Double> DEFAULT_PARTICLE_COLOR_CODEC = Codec.DOUBLE.fieldOf("default_particle_color");

    public static final MapCodec<TateroidParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PARTICLE_CODEC.forGetter(TateroidParticleSpawner::getParticleEffect),
                PLAYER_PARTICLE_RATE_CODEC.forGetter(TateroidParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(TateroidParticleSpawner::getBlockParticleChance),
                DEFAULT_PARTICLE_COLOR_CODEC.forGetter(spawner -> spawner.defaultParticleColor)
        ).apply(instance, TateroidParticleSpawner::new)
    );

    private final double defaultParticleColor;

    public TateroidParticleSpawner(ParticleEffect particleEffect, int playerParticleRate, int blockParticleChance, double defaultParticleColor) {
        super(particleEffect, playerParticleRate, blockParticleChance);

        this.defaultParticleColor = defaultParticleColor;
    }

    @Override
    protected double getParticleSpeed(TaterParticleContext context) {
        return context.getBlockEntity(NEBlocks.TATEROID_ENTITY)
                .map(blockEntity -> blockEntity.getParticleSpeed())
                .orElse(this.defaultParticleColor);
    }

    private Vec3d getPos(TaterParticleContext context) {
        var pos = context.getPos();

        if (context instanceof TaterParticleContext.Block) {
            return pos;
        }

        var box = context.getBox();
        var random = context.world().getRandom();

        double deltaX = box.getLengthX() / 2d;
        double deltaY = box.getLengthY() / 2d;
        double deltaZ = box.getLengthZ() / 2d;

        return pos.add(random.nextGaussian() * deltaX, random.nextGaussian() * deltaY, random.nextGaussian() * deltaZ);
    }

    @Override
    protected void spawn(TaterParticleContext context) {
        double particleSpeed = this.getParticleSpeed(context);

        if (particleSpeed < 0) {
            super.spawn(context);
            return;
        }

        var pos = this.getPos(context);
        var particleEffect = this.getParticleEffect(context);

        if (particleEffect != null) {
            context.world().spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), 0, 1, 0, 0, particleSpeed);
        }
    }

    @Override
    public MapCodec<? extends TateroidParticleSpawner> getCodec() {
        return CODEC;
    }
}
