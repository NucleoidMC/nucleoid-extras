package xyz.nucleoid.extras.integrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.extras.util.ExtraCodecs;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record IntegrationsConfig(
        String channel,
        String host, int port,
        @Nullable String serverIp,
        boolean sendPlayers, boolean sendGames, boolean sendChat,
        boolean sendLifecycle, boolean sendPerformance,
        boolean acceptRemoteCommands, boolean sendStatistics,
        Map<String, Integer> discordPermissionMap, Map<String, String> discordRoleMap
) {
    public static final Codec<IntegrationsConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.STRING.fieldOf("channel").forGetter(IntegrationsConfig::channel),
                Codec.STRING.fieldOf("host").forGetter(IntegrationsConfig::host),
                Codec.INT.fieldOf("port").forGetter(IntegrationsConfig::port),
                Codec.STRING.optionalFieldOf("server_ip").forGetter(config -> Optional.ofNullable(config.serverIp())),
                Codec.BOOL.optionalFieldOf("send_players", true).forGetter(IntegrationsConfig::sendPlayers),
                Codec.BOOL.optionalFieldOf("send_games", true).forGetter(IntegrationsConfig::sendGames),
                Codec.BOOL.optionalFieldOf("send_chat", true).forGetter(IntegrationsConfig::sendChat),
                Codec.BOOL.optionalFieldOf("send_lifecycle", true).forGetter(IntegrationsConfig::sendLifecycle),
                Codec.BOOL.optionalFieldOf("send_performance", true).forGetter(IntegrationsConfig::sendPerformance),
                Codec.BOOL.optionalFieldOf("accept_remote_commands", false).forGetter(IntegrationsConfig::acceptRemoteCommands),
                Codec.BOOL.optionalFieldOf("send_statistics", false).forGetter(IntegrationsConfig::sendStatistics),
                Codec.unboundedMap(Codec.STRING, Codec.intRange(0, 4)).optionalFieldOf("discord_permission_map", Collections.emptyMap()).forGetter(IntegrationsConfig::discordPermissionMap),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("discord_role_map", Collections.emptyMap()).forGetter(IntegrationsConfig::discordRoleMap)
        ).apply(instance, IntegrationsConfig::new)
    );

    private IntegrationsConfig(String channel, String host, int port, Optional<String> serverIp, boolean sendPlayers, boolean sendGames, boolean sendChat, boolean sendLifecycle, boolean sendPerformance, boolean acceptRemoteCommands, boolean sendStatistics, Map<String, Integer> discordPermissionMap, Map<String, String> discordRoleMap) {
        this(channel, host, port, serverIp.orElse(null), sendPlayers, sendGames, sendChat, sendLifecycle, sendPerformance, acceptRemoteCommands, sendStatistics, discordPermissionMap, discordRoleMap);
    }
}
