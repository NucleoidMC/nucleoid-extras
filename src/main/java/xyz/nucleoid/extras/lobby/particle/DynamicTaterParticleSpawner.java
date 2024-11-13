package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.dynamic.Codecs;

public abstract class DynamicTaterParticleSpawner extends TaterParticleSpawner {
    public static final int DEFAULT_PLAYER_PARTICLE_RATE = 2;
    public static final int DEFAULT_BLOCK_PARTICLE_CHANCE = 1;

    protected static final MapCodec<Integer> PLAYER_PARTICLE_RATE_CODEC = Codecs.POSITIVE_INT.optionalFieldOf("player_particle_rate", DEFAULT_PLAYER_PARTICLE_RATE);
    protected static final MapCodec<Integer> BLOCK_PARTICLE_CHANCE_CODEC = Codecs.POSITIVE_INT.optionalFieldOf("block_particle_chance", DEFAULT_BLOCK_PARTICLE_CHANCE);

    private final int playerParticleRate;
    private final int blockParticleChance;

    public DynamicTaterParticleSpawner(int playerParticleRate, int blockParticleChance) {
        this.playerParticleRate = playerParticleRate;
        this.blockParticleChance = blockParticleChance;
    }

    protected final int getPlayerParticleRate() {
        return this.playerParticleRate;
    }

    protected final int getBlockParticleChance() {
        return this.blockParticleChance;
    }

    @Override
    public boolean shouldSpawn(TaterParticleContext context) {
        if (context instanceof TaterParticleContext.Player playerContext) {
            return playerContext.player().age % this.playerParticleRate == 0;
        } else if (context instanceof TaterParticleContext.Block) {
            return context.world().getRandom().nextInt(this.blockParticleChance) == 0;
        }

        return super.shouldSpawn(context);
    }
    @Override
    public abstract MapCodec<? extends DynamicTaterParticleSpawner> getCodec();
}
