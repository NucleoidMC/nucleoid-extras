package xyz.nucleoid.extras.integrations.game;

import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.stats.GameStatisticBundle;
import xyz.nucleoid.plasmid.game.stats.StatisticKey;

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
                player.sendMessage(Text.literal("+--------------------------------------+")
                        .formatted(Formatting.DARK_GRAY), false);

                var bundleName = Text.translatable(GameStatisticBundle.getTranslationKey(namespace));

                player.sendMessage(Text.translatable("text.nucleoid_extras.statistics.bundle_header", bundleName)
                        .formatted(Formatting.GREEN), false);

                stats.visitAllStatistics((key, value) -> {
                    if (!key.hidden()) {
                        player.sendMessage(Text.translatable("text.nucleoid_extras.statistics.stat",
                                Text.translatable(key.getTranslationKey()), convertForDisplay(key.id(), value)), false);
                    }
                });

                player.sendMessage(Text.literal("+--------------------------------------+")
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
        space.getPlayers().sendMessage(Text.translatable("text.nucleoid_extras.statistics.web_url", gameId)
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

    public static Text convertForDisplay(Identifier key, Number number) {
        String base;

        if (key.getPath().endsWith("_time")) {
            var seconds = number.doubleValue() / 20;
            var text = Text.empty();
            if (seconds > 60) {
               text.append(Text.translatable("gui.minutes", MathHelper.floor(seconds / 60)));
            }

            if (seconds % 60 > 0.01 || text.getSiblings().isEmpty()) {
                if (!text.getSiblings().isEmpty()) {
                    text.append(" ");
                }
                text.append(Text.translatable("text.nucleoid_extras.seconds", Text.literal((seconds - (int) seconds >= 0.005) ? String.format("%.2f", seconds % 60) : ("" + ((int)seconds) % 60))));
            }
            return text;
        }

        var doubleDisplay = Math.abs(number.doubleValue() - number.intValue()) >= 0.005;

        if (doubleDisplay) {
            base = String.format("%.2f", number.doubleValue());
        } else {
            base = Integer.toString(number.intValue());
        }

        return Text.literal(base);
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
