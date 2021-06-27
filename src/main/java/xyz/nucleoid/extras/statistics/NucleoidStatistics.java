package xyz.nucleoid.extras.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.stats.GameStatisticBundle;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NucleoidStatistics {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    private static NucleoidStatistics instance;
    private static boolean initialized;

    private final StatisticsBackendConfig config;

    private NucleoidStatistics(StatisticsBackendConfig config) {
        this.config = config;
        this.attachEventListener();
    }

    private void attachEventListener() {
        GameEvents.CLOSING.register((space, reason) -> {
            // TODO: Should we only submit statistics on reason == FINISHED?
            for (Map.Entry<String, GameStatisticBundle> entry : space.getAllStatistics().entrySet()) {
                this.submitStatisticBundle(entry.getKey(), entry.getValue());
            }
        });
    }

    private void submitStatisticBundle(String namespace, GameStatisticBundle bundle) {
        Util.getIoWorkerExecutor().execute(() -> {
            LOGGER.debug("Submitting statistic bundle for '{}'...", namespace);

            JsonObject body = new JsonObject();
            body.addProperty("server_name", this.config.getServerName());
            body.addProperty("namespace", namespace);
            body.add("stats", bundle.encodeBundle());

            try {
                URL url = new URL(this.config.getBackendUrl() + "/stats/upload");

                HttpURLConnection conn = ((HttpURLConnection) url.openConnection());
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("User-Agent", "nucleoid-extras v1 (https://github.com/NucleoidMC/nucleoid-extras)");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", this.config.getAccessToken());

                try (OutputStream out = conn.getOutputStream()) {
                    IOUtils.write(GSON.toJson(body), out, StandardCharsets.UTF_8);
                }

                LOGGER.debug("Received status {} from backend!", conn.getResponseCode());
            } catch (IOException e) {
                LOGGER.error("Failed to upload statistics bundle for " + namespace, e);
            }
        });
    }

    public static void register() {
        initialized = true;
        StatisticsBackendConfig config = NucleoidExtrasConfig.get().getStatisticsBackend();
        instance = config != null ? new NucleoidStatistics(config) : null;
    }
}
