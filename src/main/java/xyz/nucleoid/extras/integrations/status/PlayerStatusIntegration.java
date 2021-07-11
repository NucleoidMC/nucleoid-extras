package xyz.nucleoid.extras.integrations.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
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
        if (config.shouldSendPlayers()) {
            IntegrationSender statusSender = integrations.openSender("status");

            PlayerStatusIntegration integration = new PlayerStatusIntegration(statusSender);
            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastCheckTime > CHECK_INTERVAL_MS) {
            this.lastCheckTime = time;

            Status status = this.checkStatus(server);
            if (status != null) {
                this.statusSender.send(status.serialize());
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
        PlayerManager playerManager = server.getPlayerManager();
        for (ServerPlayerEntity player : playerManager.getPlayerList()) {
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
            JsonObject root = new JsonObject();

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
                return this.players.equals(status.players);
            }
            return false;
        }
    }
}
