package xyz.nucleoid.extras.error;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joptsimple.internal.Strings;
import org.jetbrains.annotations.Nullable;

public final record ErrorReportingConfig(
        String discordWebhookUrl
) {
    public static final Codec<ErrorReportingConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.optionalFieldOf("discord_webhook_url", "").forGetter(config -> config.discordWebhookUrl)
        ).apply(instance, ErrorReportingConfig::new);
    });

    public static final ErrorReportingConfig NONE = new ErrorReportingConfig(null);

    @Nullable
    public DiscordGameErrorHandler openErrorHandler(String source) {
        var webhook = this.openDiscordWebhook();
        return webhook != null ? new DiscordGameErrorHandler(webhook, source) : null;
    }

    @Nullable
    public DiscordWebhook openDiscordWebhook() {
        var url = this.discordWebhookUrl();
        return url != null ? DiscordWebhook.open(url) : null;
    }

    @Override
    @Nullable
    public String discordWebhookUrl() {
        var url = this.discordWebhookUrl;
        return !Strings.isNullOrEmpty(url) ? url : null;
    }
}
