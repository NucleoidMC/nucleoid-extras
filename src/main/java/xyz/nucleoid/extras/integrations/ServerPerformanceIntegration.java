package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.MetricsData;
import xyz.nucleoid.extras.mixin.MetricsDataAccessor;
import xyz.nucleoid.extras.mixin.MinecraftServerAccessor;
import xyz.nucleoid.extras.mixin.ServerWorldAccessor;

public final class ServerPerformanceIntegration {
    private static final long SEND_INTERVAL_MS = 5 * 60 * 1000;

    private final IntegrationSender performanceSender;
    private long lastSendTime;

    private ServerPerformanceIntegration(IntegrationSender performanceSender) {
        this.performanceSender = performanceSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (!config.shouldSendPerformance()) {
            return;
        }

        IntegrationSender performanceSender = integrations.openSender("performance");

        ServerPerformanceIntegration integration = new ServerPerformanceIntegration(performanceSender);
        ServerTickEvents.END_SERVER_TICK.register(integration::tick);
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastSendTime > SEND_INTERVAL_MS) {
            this.lastSendTime = time;

            float averageTickMs = getAverageTickMs(((MinecraftServerAccessor) server).nucleoid$getMetricsData());
            int tps = averageTickMs != 0 ? (int) Math.min(1000.0F / averageTickMs, 20) : 20;

            int dimensions = 0;
            int entities = 0;
            int chunks = 0;

            for (ServerWorld world : server.getWorlds()) {
                dimensions += 1;
                entities += ((ServerWorldAccessor) world).nucleoid$getEntitiesById().size();
                chunks += world.getChunkManager().getLoadedChunkCount();
            }

            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long usedMemory = totalMemory - runtime.freeMemory();

            JsonObject payload = new JsonObject();
            payload.addProperty("average_tick_ms", averageTickMs);
            payload.addProperty("tps", tps);
            payload.addProperty("dimensions", dimensions);
            payload.addProperty("entities", entities);
            payload.addProperty("chunks", chunks);
            payload.addProperty("used_memory", usedMemory);
            payload.addProperty("total_memory", totalMemory);

            this.performanceSender.send(payload);
        }
    }

    private static float getAverageTickMs(MetricsData data) {
        long[] samples = ((MetricsDataAccessor) data).nucleoid$getSamples();
        long total = 0;
        for (long sample : samples) {
            total += sample;
        }
        double averageTickNs = (double) total / samples.length;
        return (float) (averageTickNs / 1000000.0);
    }
}
