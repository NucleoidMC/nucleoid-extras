package xyz.nucleoid.extras.lobby.particle;

public abstract class DynamicTaterParticleSpawner extends TaterParticleSpawner {
    protected static final int DEFAULT_PLAYER_PARTICLE_RATE = 2;
    protected static final int DEFAULT_BLOCK_PARTICLE_CHANCE = 1;

    private final int playerParticleRate;
    private final int blockParticleChance;

    public DynamicTaterParticleSpawner(int playerParticleRate, int blockParticleChance) {
        this.playerParticleRate = playerParticleRate;
        this.blockParticleChance = blockParticleChance;
    }

    public DynamicTaterParticleSpawner(int playerParticleRate) {
        this(playerParticleRate, DEFAULT_BLOCK_PARTICLE_CHANCE);
    }

    public DynamicTaterParticleSpawner() {
        this(DEFAULT_PLAYER_PARTICLE_RATE);
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
}
