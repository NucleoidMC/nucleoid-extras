package xyz.nucleoid.extras.statistics;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.stats.GameStatisticBundle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NucleoidStatistics {
    private static final Logger LOGGER = LogManager.getLogger();

    private static NucleoidStatistics instance;
    private static boolean initialized;

    private final IntegrationSender statisticSender;

    private final List<JsonObject> queuedBundles = new ArrayList<>();

    private NucleoidStatistics(IntegrationSender statisticSender) {
        this.statisticSender = statisticSender;
        this.attachEventListener();
    }

    private void attachEventListener() {
        GameEvents.CLOSING.register((space, reason) -> {
            if (reason == GameCloseReason.FINISHED) {
                space.visitAllStatistics(this::submitStatisticBundle);
            }
        });
    }

    private void submitStatisticBundle(String namespace, GameStatisticBundle bundle) {
        LOGGER.debug("Submitting statistic bundle for '{}'...", namespace);

        JsonObject body = new JsonObject();
        body.addProperty("namespace", namespace);
        body.add("stats", bundle.encodeBundle());
        this.sendBundle(body);
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

    public static void bind(NucleoidIntegrations integrations) {
        initialized = true;
        instance = new NucleoidStatistics(integrations.openSender("upload_statistics"));
        integrations.bindConnectionOpen(instance::onConnectionOpen);
    }
}
