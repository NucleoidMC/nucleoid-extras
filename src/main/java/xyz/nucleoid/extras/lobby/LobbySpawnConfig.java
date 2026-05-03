package xyz.nucleoid.extras.lobby;

import java.util.Collections;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LobbySpawnConfig(
    Vec3 pos,
    float yaw,
    float pitch,
    Optional<GameType> gameMode
) {
    public static final Codec<LobbySpawnConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Vec3.CODEC.fieldOf("pos").forGetter(LobbySpawnConfig::pos),
                Codec.FLOAT.optionalFieldOf("yaw", 0f).forGetter(LobbySpawnConfig::yaw),
                Codec.FLOAT.optionalFieldOf("pitch", 0f).forGetter(LobbySpawnConfig::pitch),
                GameType.CODEC.optionalFieldOf("game_mode").forGetter(LobbySpawnConfig::gameMode)
        ).apply(instance, LobbySpawnConfig::new)
    );

    public void teleport(ServerPlayer player, ServerLevel world) {
        player.teleportTo(world, this.pos.x(), this.pos.y(), this.pos.z(), Collections.emptySet(), this.yaw, this.pitch, true);
    }

    public void changeGameMode(ServerPlayer player, GameType fallback) {
        player.setGameMode(this.gameMode.orElse(fallback));
    }
}
