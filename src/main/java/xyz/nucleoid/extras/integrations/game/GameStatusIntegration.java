package xyz.nucleoid.extras.integrations.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.plasmid.game.ConfiguredGame;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

import java.util.ArrayList;
import java.util.Collection;
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
        if (config.shouldSendGames()) {
            IntegrationSender statusSender = integrations.openSender("status");

            GameStatusIntegration integration = new GameStatusIntegration(statusSender);
            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastCheckTime > CHECK_INTERVAL_MS) {
            this.lastCheckTime = time;

            Status status = this.checkStatus();
            if (status != null) {
                this.statusSender.send(status.serialize());
            }
        }
    }

    @Nullable
    private Status checkStatus() {
        Status swap = this.lastStatus;
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
        Collection<ManagedGameSpace> games = ManagedGameSpace.getOpen();
        for (ManagedGameSpace game : games) {
            status.addGame(game.getGameConfig(), game.getPlayerCount());
        }
    }

    static final class Status {
        final List<GameEntry> games = new ArrayList<>();

        void clear() {
            this.games.clear();
        }

        void addGame(ConfiguredGame<?> game, int playerCount) {
            this.games.add(new GameEntry(game.getName(), game.getType().getIdentifier(), playerCount));
        }

        JsonObject serialize() {
            JsonObject root = new JsonObject();

            JsonArray gamesArray = new JsonArray();
            for (GameEntry game : this.games) {
                gamesArray.add(game.serialize());
            }

            root.add("games", gamesArray);

            return root;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Status) {
                Status status = (Status) obj;
                return this.games.equals(status.games);
            }
            return false;
        }
    }

    static final class GameEntry {
        final String name;
        final Identifier typeId;
        final int playerCount;

        GameEntry(String name, Identifier typeId, int playerCount) {
            this.name = name;
            this.typeId = typeId;
            this.playerCount = playerCount;
        }

        JsonObject serialize() {
            JsonObject root = new JsonObject();
            root.addProperty("name", this.name);
            root.addProperty("type", this.typeId.toString());
            root.addProperty("player_count", this.playerCount);
            return root;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof GameEntry) {
                GameEntry game = (GameEntry) obj;
                return this.playerCount == game.playerCount && this.name.equals(game.name) && this.typeId.equals(game.typeId);
            }
            return false;
        }
    }
}
