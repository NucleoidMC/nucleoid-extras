package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleIntegration {
    private static boolean crashed;

    private final IntegrationSender lifecycleStartSender;
    private final IntegrationSender lifecycleStopSender;

    private boolean queuedStarted;

    private ServerLifecycleIntegration(IntegrationSender lifecycleStartSender, IntegrationSender lifecycleStopSender) {
        this.lifecycleStartSender = lifecycleStartSender;
        this.lifecycleStopSender = lifecycleStopSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.shouldSendLifecycle()) {
            IntegrationSender lifecycleStartSender = integrations.openSender("lifecycle_start");
            IntegrationSender lifecycleStopSender = integrations.openSender("lifecycle_stop");

            ServerLifecycleIntegration integration = new ServerLifecycleIntegration(lifecycleStartSender, lifecycleStopSender);

            integrations.bindConnectionOpen(integration::onConnectionOpen);

            ServerLifecycleEvents.SERVER_STARTED.register(integration::onServerStarted);
            ServerLifecycleEvents.SERVER_STOPPING.register(integration::onServerStopping);
        }
    }

    public static void setCrashed() {
        crashed = true;
    }

    private void onConnectionOpen() {
        if (this.queuedStarted && this.trySendStart()) {
            this.queuedStarted = false;
        }
    }

    private void onServerStarted(MinecraftServer server) {
        if (!this.trySendStart()) {
            this.queuedStarted = true;
        }
    }

    private void onServerStopping(MinecraftServer server) {
        boolean crashed = ServerLifecycleIntegration.crashed;
        ServerLifecycleIntegration.crashed = false;

        this.trySendStop(crashed);
        this.queuedStarted = false;
    }

    private boolean trySendStart() {
        return this.lifecycleStartSender.send(new JsonObject());
    }

    private boolean trySendStop(boolean crash) {
        JsonObject payload = new JsonObject();
        payload.addProperty("crash", crash);
        return this.lifecycleStopSender.send(payload);
    }
}
