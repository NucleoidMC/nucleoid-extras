package xyz.nucleoid.extras.integrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class IntegrationsConfig {
    public static final Codec<IntegrationsConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("channel").forGetter(config -> config.channel),
                Codec.STRING.fieldOf("host").forGetter(config -> config.host),
                Codec.INT.fieldOf("port").forGetter(config -> config.port),
                Codec.STRING.optionalFieldOf("server_ip").forGetter(config -> Optional.ofNullable(config.serverIp)),
                Codec.BOOL.optionalFieldOf("send_players", true).forGetter(config -> config.sendPlayers),
                Codec.BOOL.optionalFieldOf("send_games", true).forGetter(config -> config.sendGames),
                Codec.BOOL.optionalFieldOf("send_chat", true).forGetter(config -> config.sendChat),
                Codec.BOOL.optionalFieldOf("send_lifecycle", true).forGetter(config -> config.sendLifecycle)
        ).apply(instance, IntegrationsConfig::new);
    });

    private final String channel;
    private final String host;
    private final int port;

    private final String serverIp;

    private final boolean sendPlayers;
    private final boolean sendGames;
    private final boolean sendChat;
    private final boolean sendLifecycle;

    private IntegrationsConfig(String channel, String host, int port, Optional<String> serverIp, boolean sendPlayers, boolean sendGames, boolean sendChat, boolean sendLifecycle) {
        this.channel = channel;
        this.host = host;
        this.port = port;
        this.serverIp = serverIp.orElse(null);
        this.sendPlayers = sendPlayers;
        this.sendGames = sendGames;
        this.sendChat = sendChat;
        this.sendLifecycle = sendLifecycle;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    @Nullable
    public String getServerIp() {
        return this.serverIp;
    }

    public boolean shouldSendPlayers() {
        return this.sendPlayers;
    }

    public boolean shouldSendGames() {
        return this.sendGames;
    }

    public boolean shouldSendChat() {
        return this.sendChat;
    }

    public boolean shouldSendLifecycle() {
        return this.sendLifecycle;
    }
}
