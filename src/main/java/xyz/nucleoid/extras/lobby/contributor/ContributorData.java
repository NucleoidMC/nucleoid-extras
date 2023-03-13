package xyz.nucleoid.extras.lobby.contributor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonHelper;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.lobby.block.ContributorStatueBlockEntity;
import xyz.nucleoid.extras.mixin.lobby.ThreadedAnvilChunkStorageAccessor;

public final class ContributorData {
    private static final Logger LOGGER = LogManager.getLogger(ContributorData.class);

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("contributor-data-fetcher")
            .setDaemon(true)
            .build()
    );

    private static final String PEOPLE_KEY = "people";

    private static final Map<String, Contributor> CONTRIBUTORS = new HashMap<>();

    // Methods for accessing data

    public static Iterable<Entry<String, Contributor>> getContributors() {
        return CONTRIBUTORS.entrySet();
    }

    public static Contributor getContributor(String id) {
        return CONTRIBUTORS.get(id);
    }

    // Methods for reloading data

    private static void addContributor(Entry<String, JsonElement> entry) {
        var result = Contributor.CODEC.decode(JsonOps.INSTANCE, entry.getValue());

        result.error().ifPresent(error -> {
            LOGGER.warn("Failed to parse contributor '{}': {}", entry.getKey(), error.message());
        });

        result.result().ifPresent(pair -> {
            CONTRIBUTORS.put(entry.getKey(), pair.getFirst());
        });
    }

    private static void refreshData() {
        CONTRIBUTORS.clear();

        try {
            var config = NucleoidExtrasConfig.get();
            if (config.contributorDataUrl() == null) return;

            var url = new URL(config.contributorDataUrl());
            var connection = (HttpsURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            try (var reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                var json = JsonHelper.deserialize(reader);
                var people = json.getAsJsonObject(PEOPLE_KEY);

                for (var entry : people.entrySet()) {
                    addContributor(entry);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to fetch contributor data", e);
        }
    }

    private static void refreshHolograms(MinecraftServer server) {
        for (var world : server.getWorlds()) {
            var chunkManager = world.getChunkManager();

            var chunkStorage = chunkManager.threadedAnvilChunkStorage;
            var accessor = (ThreadedAnvilChunkStorageAccessor) (Object) chunkStorage;

            for (var holder : accessor.callEntryIterator()) {
                var chunk = holder.getWorldChunk();

                if (chunk != null) {
                    for (var entity : chunk.getBlockEntities().values()) {
                        if (entity instanceof ContributorStatueBlockEntity statue) {
                            statue.removeHolograms();
                            statue.spawnHolograms();
                        }
                    }
                }
            }
        }
    }

    private static void refreshAll(MinecraftServer server, boolean initial) {
        EXECUTOR.execute(() -> {
            refreshData();

            if (!initial) {
                refreshHolograms(server);
            }
        });
        
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> refreshAll(server, true));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> refreshAll(server, false));
    }
}
