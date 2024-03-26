package xyz.nucleoid.extras.error;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.codecs.MoreCodecs;
import java.net.URL;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record ErrorReportingConfig(
        Optional<URL> discordWebhookUrl
) {
    public static final Codec<ErrorReportingConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                MoreCodecs.url("https").optionalFieldOf("discord_webhook_url").forGetter(config -> config.discordWebhookUrl)
        ).apply(instance, ErrorReportingConfig::new)
    );

    public static final ErrorReportingConfig NONE = new ErrorReportingConfig(Optional.empty());

    @Nullable
    public DiscordGameErrorHandler openErrorHandler(String source) {
        var webhook = this.openDiscordWebhook();
        return webhook != null ? new DiscordGameErrorHandler(webhook, source) : null;
    }

    @Nullable
    public DiscordWebhook openDiscordWebhook() {
        var url = this.discordWebhookUrl();
        return url.map(DiscordWebhook::open).orElse(null);
    }
}
