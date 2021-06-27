package xyz.nucleoid.extras.statistics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StatisticsBackendConfig {
    public static final Codec<StatisticsBackendConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("backend_url").forGetter(StatisticsBackendConfig::getBackendUrl),
            Codec.STRING.fieldOf("server_name").forGetter(StatisticsBackendConfig::getServerName),
            Codec.STRING.fieldOf("access_token").forGetter(StatisticsBackendConfig::getAccessToken)
    ).apply(instance, StatisticsBackendConfig::new));

    private final String backendUrl;
    private final String serverName;
    private final String accessToken;

    private StatisticsBackendConfig(String backendUrl, String serverName, String accessToken) {
        this.backendUrl = backendUrl;
        this.serverName = serverName;
        this.accessToken = accessToken;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    public String getServerName() {
        return serverName;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
