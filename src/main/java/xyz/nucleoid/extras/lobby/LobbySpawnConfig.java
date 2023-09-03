package xyz.nucleoid.extras.lobby;

import java.util.Collections;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public record LobbySpawnConfig(
    Vec3d pos,
    float yaw,
    float pitch,
    Optional<GameMode> gameMode
) {
    public static final Codec<LobbySpawnConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Vec3d.CODEC.fieldOf("pos").forGetter(LobbySpawnConfig::pos),
                Codec.FLOAT.optionalFieldOf("yaw", 0f).forGetter(LobbySpawnConfig::yaw),
                Codec.FLOAT.optionalFieldOf("pitch", 0f).forGetter(LobbySpawnConfig::pitch),
                GameMode.CODEC.optionalFieldOf("game_mode").forGetter(LobbySpawnConfig::gameMode)
        ).apply(instance, LobbySpawnConfig::new)
    );

    public void teleport(ServerPlayerEntity player, ServerWorld world) {
        player.teleport(world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Collections.emptySet(), this.yaw, this.pitch);
    }

    public void changeGameMode(ServerPlayerEntity player, GameMode fallback) {
        player.changeGameMode(this.gameMode.orElse(fallback));
    }
}
