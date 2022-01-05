package xyz.nucleoid.extras.integrations.game;

import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.stats.GameStatisticBundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StatisticsIntegration {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IntegrationSender statisticSender;

    private final List<JsonObject> queuedBundles = new ArrayList<>();

    private StatisticsIntegration(IntegrationSender statisticSender) {
        this.statisticSender = statisticSender;
    }

    private void handleStatisticsBundle(GameSpace space, String namespace, GameStatisticBundle bundle) {
        for (ServerPlayerEntity player : space.getPlayers()) {
            var stats = bundle.forPlayer(player);
            if (!stats.isEmpty()) {
                player.sendMessage(new LiteralText("+--------------------------------------+")
                        .formatted(Formatting.DARK_GRAY), false);

                var bundleName = new TranslatableText(GameStatisticBundle.getTranslationKey(namespace));

                player.sendMessage(new TranslatableText("text.nucleoid_extras.statistics.bundle_header", bundleName)
                        .formatted(Formatting.GREEN), false);

                stats.visitAllStatistics((key, value) -> {
                    if (!key.hidden()) {
                        player.sendMessage(new TranslatableText("text.nucleoid_extras.statistics.stat",
                                new TranslatableText(key.getTranslationKey()), roundForDisplay(value)), false);
                    }
                });

                player.sendMessage(new LiteralText("+--------------------------------------+")
                        .formatted(Formatting.DARK_GRAY), false);
            }
        }

        // Do not send statistics for anonymous games to the backend
        if (space.getMetadata().sourceConfig().source() == null) return;

        UUID gameId = space.getMetadata().id();

        LOGGER.debug("Submitting statistic bundle for '{}' game id: {}...", namespace, gameId);

        JsonObject body = new JsonObject();
        JsonObject bundleObject = new JsonObject();
        bundleObject.addProperty("namespace", namespace);
        bundleObject.add("stats", bundle.encode());
        body.add("bundle", bundleObject);
        body.addProperty("game_id", gameId.toString());
        this.sendBundle(body);
        space.getPlayers().sendMessage(new TranslatableText("text.nucleoid_extras.statistics.web_url", gameId)
                .formatted(Formatting.GRAY, Formatting.ITALIC)
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://stats.nucleoid.xyz/games/" + gameId))));
    }

    private void sendBundle(JsonObject bundle) {
        if (!this.statisticSender.send(bundle)) {
            this.queuedBundles.add(bundle);
        }
    }

    private void onConnectionOpen() {
        Iterator<JsonObject> iter = this.queuedBundles.iterator();
        while (iter.hasNext()) {
            JsonObject bundle = iter.next();
            iter.remove();
            this.sendBundle(bundle);
        }
    }

    private static String roundForDisplay(Number number) {
        if (number instanceof Double d) {
            return String.format("%.2f", d);
        } else if (number instanceof Float f) {
            return String.format("%.2f", f);
        } else {
            return String.format("%s", number);
        }
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.sendStatistics()) {
            var instance = new StatisticsIntegration(integrations.openSender("upload_statistics"));
            integrations.bindConnectionOpen(instance::onConnectionOpen);
            GameEvents.CLOSING.register((space, reason) -> {
                if (reason == GameCloseReason.FINISHED) {
                    space.getStatistics().visitAll((namespace, bundle) ->
                            instance.handleStatisticsBundle(space, namespace, bundle));
                }
            });
        }
    }
}
