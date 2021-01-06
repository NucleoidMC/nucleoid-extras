package xyz.nucleoid.extras.integrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class IntegrationsConfig {
    public static final Codec<IntegrationsConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("channel").forGetter(config -> config.channel),
                Codec.STRING.fieldOf("host").forGetter(config -> config.host),
                Codec.INT.fieldOf("port").forGetter(config -> config.port),
                Codec.BOOL.optionalFieldOf("send_players", true).forGetter(config -> config.sendPlayers),
                Codec.BOOL.optionalFieldOf("send_games", true).forGetter(config -> config.sendGames),
                Codec.BOOL.optionalFieldOf("send_chat", true).forGetter(config -> config.sendChat)
        ).apply(instance, IntegrationsConfig::new);
    });

    private final String channel;
    private final String host;
    private final int port;

    private final boolean sendPlayers;
    private final boolean sendGames;
    private final boolean sendChat;

    private IntegrationsConfig(String channel, String host, int port, boolean sendPlayers, boolean sendGames, boolean sendChat) {
        this.channel = channel;
        this.host = host;
        this.port = port;
        this.sendPlayers = sendPlayers;
        this.sendGames = sendGames;
        this.sendChat = sendChat;
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

    public boolean shouldSendPlayers() {
        return this.sendPlayers;
    }

    public boolean shouldSendGames() {
        return this.sendGames;
    }

    public boolean shouldSendChat() {
        return this.sendChat;
    }
}
