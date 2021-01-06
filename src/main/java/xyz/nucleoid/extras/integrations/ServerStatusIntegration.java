package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.ConfiguredGame;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class ServerStatusIntegration {
    private static final long CHECK_INTERVAL_MS = 10 * 1000;

    private final Consumer<JsonObject> statusSender;
    private final boolean players;
    private final boolean games;

    private long lastCheckTime;

    private Status currentStatus = new Status();
    private Status lastStatus = new Status();

    private ServerStatusIntegration(Consumer<JsonObject> statusSender, boolean players, boolean games) {
        this.statusSender = statusSender;
        this.players = players;
        this.games = games;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        boolean players = config.shouldSendPlayers();
        boolean games = config.shouldSendGames();
        if (!players && !games) {
            return;
        }

        Consumer<JsonObject> statusSender = integrations.openSender("status");

        ServerStatusIntegration integration = new ServerStatusIntegration(statusSender, players, games);
        ServerTickEvents.END_SERVER_TICK.register(integration::tick);
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastCheckTime > CHECK_INTERVAL_MS) {
            this.lastCheckTime = time;

            Status status = this.checkStatus(server);
            if (status != null) {
                this.statusSender.accept(status.serialize());
            }
        }
    }

    @Nullable
    private Status checkStatus(MinecraftServer server) {
        Status swap = this.lastStatus;
        swap.clear();

        this.lastStatus = this.currentStatus;
        this.currentStatus = swap;

        this.buildStatus(server, this.currentStatus);

        if (!this.currentStatus.equals(this.lastStatus)) {
            return this.currentStatus;
        } else {
            return null;
        }
    }

    private void buildStatus(MinecraftServer server, Status status) {
        if (this.games) {
            Collection<ManagedGameSpace> games = ManagedGameSpace.getOpen();
            for (ManagedGameSpace game : games) {
                status.addGame(game.getGameConfig(), game.getPlayerCount());
            }
        }

        if (this.players) {
            PlayerManager playerManager = server.getPlayerManager();
            for (ServerPlayerEntity player : playerManager.getPlayerList()) {
                status.addPlayer(player.getGameProfile());
            }
        }
    }

    static final class Status {
        final List<GameProfile> players = new ArrayList<>();
        final List<GameEntry> games = new ArrayList<>();

        void clear() {
            this.players.clear();
            this.games.clear();
        }

        void addGame(ConfiguredGame<?> game, int playerCount) {
            this.games.add(new GameEntry(game.getName(), game.getType().getIdentifier(), playerCount));
        }

        void addPlayer(GameProfile player) {
            this.players.add(player);
        }

        JsonObject serialize() {
            JsonObject root = new JsonObject();

            JsonArray gamesArray = new JsonArray();
            for (GameEntry game : this.games) {
                gamesArray.add(game.serialize());
            }

            root.add("games", gamesArray);

            JsonArray playerArray = new JsonArray();
            for (GameProfile player : this.players) {
                JsonObject playerRoot = new JsonObject();
                playerRoot.addProperty("id", player.getId().toString());
                playerRoot.addProperty("name", player.getName());
                playerArray.add(playerRoot);
            }

            root.add("players", playerArray);

            return root;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Status) {
                Status status = (Status) obj;
                return this.players.equals(status.players) && this.games.equals(status.games);
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
