package xyz.nucleoid.extras.integrations.game;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.plasmid.api.event.GameEvents;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.stats.GameStatisticBundle;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class StatisticsIntegration {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IntegrationSender statisticSender;

    private final List<JsonObject> queuedBundles = new ArrayList<>();

    private StatisticsIntegration(IntegrationSender statisticSender) {
        this.statisticSender = statisticSender;
    }

    private void handleStatisticsBundle(GameSpace space, String namespace, GameStatisticBundle bundle) {
        for (ServerPlayer player : space.getPlayers()) {
            var stats = bundle.forPlayer(player);
            if (!stats.isEmpty()) {
                player.displayClientMessage(Component.literal("+--------------------------------------+")
                        .withStyle(ChatFormatting.DARK_GRAY), false);

                var bundleName = Component.translatable(GameStatisticBundle.getTranslationKey(namespace));

                player.displayClientMessage(Component.translatable("text.nucleoid_extras.statistics.bundle_header", bundleName)
                        .withStyle(ChatFormatting.GREEN), false);

                stats.visitAllStatistics((key, value) -> {
                    if (!key.hidden()) {
                        player.displayClientMessage(Component.translatable("text.nucleoid_extras.statistics.stat",
                                Component.translatable(key.getTranslationKey()), convertForDisplay(key.id(), value)), false);
                    }
                });

                player.displayClientMessage(Component.literal("+--------------------------------------+")
                        .withStyle(ChatFormatting.DARK_GRAY), false);
            }
        }

        // Do not send statistics for anonymous games to the backend
        if (!space.getMetadata().sourceConfig().unwrapKey().isPresent()) return;

        UUID gameId = space.getMetadata().id();

        LOGGER.debug("Submitting statistic bundle for '{}' game id: {}...", namespace, gameId);

        JsonObject body = new JsonObject();
        JsonObject bundleObject = new JsonObject();
        bundleObject.addProperty("namespace", namespace);
        bundleObject.add("stats", bundle.encode());
        body.add("bundle", bundleObject);
        body.addProperty("game_id", gameId.toString());
        this.sendBundle(body);
        space.getPlayers().sendMessage(Component.translatable("text.nucleoid_extras.statistics.web_url")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                .withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(
                        URI.create("https://stats.nucleoid.xyz/games/" + gameId)))));
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

    public static Component convertForDisplay(ResourceLocation key, Number number) {
        String base;

        if (key.getPath().endsWith("_time")) {
            var seconds = number.doubleValue() / 20;
            var text = Component.empty();
            if (seconds > 60) {
               text.append(Component.translatableEscape("gui.minutes", Mth.floor(seconds / 60)));
            }

            if (seconds % 60 > 0.01 || text.getSiblings().isEmpty()) {
                if (!text.getSiblings().isEmpty()) {
                    text.append(" ");
                }
                text.append(Component.translatableEscape("text.nucleoid_extras.seconds", Component.literal((seconds - (int) seconds >= 0.005) ? String.format("%.2f", seconds % 60) : ("" + ((int)seconds) % 60))));
            }
            return text;
        }

        var doubleDisplay = Math.abs(number.doubleValue() - number.intValue()) >= 0.005;

        if (doubleDisplay) {
            base = String.format("%.2f", number.doubleValue());
        } else {
            base = Integer.toString(number.intValue());
        }

        return Component.literal(base);
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
