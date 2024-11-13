package xyz.nucleoid.extras.lobby.particle;

import com.mojang.serialization.MapCodec;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.api.util.TinyRegistry;

import java.util.function.Function;

public final class TaterParticleSpawnerTypes {
    public static final TinyRegistry<MapCodec<? extends TaterParticleSpawner>> REGISTRY = TinyRegistry.create();

    public static final MapCodec<TaterParticleSpawner> CODEC = TaterParticleSpawnerTypes.REGISTRY.dispatchMap(TaterParticleSpawner::getCodec, Function.identity());

    private TaterParticleSpawnerTypes() {
    }

    public static void register() {
        register("color_pattern", ColorPatternTaterParticleSpawner.CODEC);
        register("entity_effect", EntityEffectTaterParticleSpawner.CODEC);
        register("light", LightTaterParticleSpawner.CODEC);
        register("lucky", LuckyTaterParticleSpawner.CODEC);
        register("random", RandomTaterParticleSpawner.CODEC);
        register("ring", RingTaterParticleSpawner.CODEC);
        register("simple_marker", SimpleMarkerTaterParticleSpawner.CODEC);
        register("simple", SimpleTaterParticleSpawner.CODEC);
        register("tateroid", TateroidParticleSpawner.CODEC);
        register("warden", WardenTaterParticleSpawner.CODEC);
    }

    private static void register(String id, MapCodec<? extends TaterParticleSpawner> codec) {
        REGISTRY.register(NucleoidExtras.identifier(id), codec);
    }
}
