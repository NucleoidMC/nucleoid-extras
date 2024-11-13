package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.tag.NEBlockTags;

public class WardenTaterParticleSpawner extends DynamicTaterParticleSpawner {
    public static final int WARDEN_PLAYER_PARTICLE_RATE = SharedConstants.TICKS_PER_SECOND;

    public static final MapCodec<WardenTaterParticleSpawner> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                PLAYER_PARTICLE_RATE_CODEC.forGetter(WardenTaterParticleSpawner::getPlayerParticleRate),
                BLOCK_PARTICLE_CHANCE_CODEC.forGetter(WardenTaterParticleSpawner::getBlockParticleChance)
        ).apply(instance, WardenTaterParticleSpawner::new)
    );

    private static final int BOX_SIZE = 16;

    public WardenTaterParticleSpawner(int playerParticleRate, int blockParticleChance) {
        super(playerParticleRate, blockParticleChance);
    }

    public WardenTaterParticleSpawner() {
        this(WARDEN_PLAYER_PARTICLE_RATE, DEFAULT_BLOCK_PARTICLE_CHANCE);
    }

    @Override
    public ParticleEffect getParticleEffect(TaterParticleContext context) {
        return getTaterVibrationParticleEffect(BlockPos.ofFloored(context.getEyePos()), context.world());
    }

    @Override
    protected double getParticleSpeed(TaterParticleContext context) {
        return 0;
    }

    @Override
    protected void spawn(TaterParticleContext context) {
        var pos = context.getEyePos();

        var particleEffect = this.getParticleEffect(context);
        double particleSpeed = this.getParticleSpeed(context);

        if (particleEffect != null) {
            context.world().spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, particleSpeed);
        }
    }

    @Override
    public MapCodec<? extends WardenTaterParticleSpawner> getCodec() {
        return CODEC;
    }

    @Nullable
    private static ParticleEffect getTaterVibrationParticleEffect(BlockPos pos, ServerWorld world) {
        var taters = new LongArrayList();

        int range = (int) (BOX_SIZE / 2d);
        for (var taterPos : BlockPos.iterateOutwards(pos, range, range, range)) {
            var state = world.getBlockState(taterPos);
            if (isVibrationTater(state)) {
                taters.add(taterPos.asLong());
            }
        }

        if (taters.isEmpty()) {
            return null;
        }

        int index = world.getRandom().nextInt(taters.size());
        var taterPos = BlockPos.fromLong(taters.getLong(index));

        return new VibrationParticleEffect(new BlockPositionSource(taterPos), (int) Math.floor(Math.sqrt(pos.getSquaredDistance(taterPos))));
    }

    private static boolean isVibrationTater(BlockState state) {
        return state.getBlock() instanceof TinyPotatoBlock && !state.isIn(NEBlockTags.NON_VIBRATING_TATERS);
    }
}
