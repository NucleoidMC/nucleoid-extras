package xyz.nucleoid.extras.integrations.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;

import java.util.ArrayList;
import java.util.List;

public final class GameStatusIntegration {
    private static final long CHECK_INTERVAL_MS = 10 * 1000;

    private final IntegrationSender statusSender;

    private long lastCheckTime;

    private Status currentStatus = new Status();
    private Status lastStatus = new Status();

    private GameStatusIntegration(IntegrationSender statusSender) {
        this.statusSender = statusSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.sendGames()) {
            var statusSender = integrations.openSender("status");

            var integration = new GameStatusIntegration(statusSender);
            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastCheckTime > CHECK_INTERVAL_MS) {
            this.lastCheckTime = time;

            var status = this.checkStatus();
            if (status != null) {
                this.statusSender.send(status.serialize());
            }
        }
    }

    @Nullable
    private Status checkStatus() {
        var swap = this.lastStatus;
        swap.clear();

        this.lastStatus = this.currentStatus;
        this.currentStatus = swap;

        this.buildStatus(this.currentStatus);

        if (!this.currentStatus.equals(this.lastStatus)) {
            return this.currentStatus;
        } else {
            return null;
        }
    }

    private void buildStatus(Status status) {
        var games = GameSpaceManager.get().getOpenGameSpaces();
        for (var game : games) {
            status.addGame(game.getMetadata().sourceConfig(), game.getPlayers().size());
        }
    }

    static final class Status {
        final List<GameEntry> games = new ArrayList<>();

        void clear() {
            this.games.clear();
        }

        void addGame(RegistryEntry<GameConfig<?>> game, int playerCount) {
            this.games.add(new GameEntry(game.value().name().getString(), game.value().type().id(), playerCount));
        }

        JsonObject serialize() {
            var root = new JsonObject();

            var gamesArray = new JsonArray();
            for (var game : this.games) {
                gamesArray.add(game.serialize());
            }

            root.add("games", gamesArray);

            return root;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Status status && this.games.equals(status.games);
        }
    }

    record GameEntry(String name, Identifier typeId, int playerCount) {
        JsonObject serialize() {
            var root = new JsonObject();
            root.addProperty("name", this.name);
            root.addProperty("type", this.typeId.toString());
            root.addProperty("player_count", this.playerCount);
            return root;
        }
    }
}
