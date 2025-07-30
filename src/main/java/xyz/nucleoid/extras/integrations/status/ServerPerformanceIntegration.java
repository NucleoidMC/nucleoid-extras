package xyz.nucleoid.extras.integrations.status;

import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;
import xyz.nucleoid.extras.event.NucleoidExtrasEvents;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.extras.mixin.ServerEntityManagerAccessor;
import xyz.nucleoid.extras.mixin.ServerWorldAccessor;

public final class ServerPerformanceIntegration {
    private static final long SEND_INTERVAL_MS = 5 * 60 * 1000;

    private final IntegrationSender performanceSender;
    private long lastSendTime;

    private ServerPerformanceIntegration(IntegrationSender performanceSender) {
        this.performanceSender = performanceSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (!config.sendPerformance()) {
            return;
        }

        var performanceSender = integrations.openSender("performance");

        var integration = new ServerPerformanceIntegration(performanceSender);
        NucleoidExtrasEvents.END_SERVER_TICK.register(integration::tick);
    }

    private void tick(MinecraftServer server) {
        long time = System.currentTimeMillis();
        if (time - this.lastSendTime > SEND_INTERVAL_MS) {
            this.lastSendTime = time;

            float averageTickMs = getAverageTickMs(((HasTickPerformanceLog) server).getTickPerformanceLog());
            int tps = averageTickMs != 0 ? (int) Math.min(1000.0F / averageTickMs, 20) : 20;

            int dimensions = 0;
            int entities = 0;
            int chunks = 0;

            for (var world : server.getWorlds()) {
                var entityManager = ((ServerWorldAccessor) world).nucleoid$getEntityManager();
                var entityIds = ((ServerEntityManagerAccessor) entityManager).nucleoid$getEntityUuids();

                dimensions += 1;
                entities += entityIds.size();
                chunks += world.getChunkManager().getLoadedChunkCount();
            }

            var runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long usedMemory = totalMemory - runtime.freeMemory();

            var payload = new JsonObject();
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

    private static float getAverageTickMs(MultiValueDebugSampleLog log) {
        try {
            long total = 0;
            for (int index = 0; index < log.getLength(); index++) {
                total += log.get(index);
            }
            double averageTickNs = (double) total / log.getLength();
            return (float) (averageTickNs / 1000000.0);
        } catch (Throwable e) {
            return 0;
        }
    }
}
