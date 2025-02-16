package xyz.nucleoid.extras.integrations.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.event.NucleoidExtrasEvents;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;

import java.util.ArrayList;
import java.util.List;

public final class PlayerStatusIntegration {
    private static final long CHECK_INTERVAL_MS = 10 * 1000;

    private final IntegrationSender statusSender;

    private long lastCheckTime;

    private Status currentStatus = new Status();
    private Status lastStatus = new Status();

    private PlayerStatusIntegration(IntegrationSender statusSender) {
        this.statusSender = statusSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.sendPlayers()) {
            var statusSender = integrations.openSender("status");

            var integration = new PlayerStatusIntegration(statusSender);
            NucleoidExtrasEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastCheckTime > CHECK_INTERVAL_MS) {
            this.lastCheckTime = time;

            var status = this.checkStatus(server);
            if (status != null) {
                this.statusSender.send(status.serialize());
            }
        }
    }

    @Nullable
    private Status checkStatus(MinecraftServer server) {
        var swap = this.lastStatus;
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
        var playerManager = server.getPlayerManager();
        for (var player : playerManager.getPlayerList()) {
            status.addPlayer(player.getGameProfile());
        }
    }

    static final class Status {
        final List<GameProfile> players = new ArrayList<>();

        void clear() {
            this.players.clear();
        }

        void addPlayer(GameProfile player) {
            this.players.add(player);
        }

        JsonObject serialize() {
            var root = new JsonObject();

            var playerArray = new JsonArray();
            for (var player : this.players) {
                var playerRoot = new JsonObject();
                playerRoot.addProperty("id", player.getId().toString());
                playerRoot.addProperty("name", player.getName());
                playerArray.add(playerRoot);
            }

            root.add("players", playerArray);

            return root;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Status status && this.players.equals(status.players);
        }
    }
}
