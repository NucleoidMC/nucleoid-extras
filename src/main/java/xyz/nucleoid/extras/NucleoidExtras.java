package xyz.nucleoid.extras;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public final class NucleoidExtras implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(NucleoidExtras::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        int ticks = server.getTicks();
        if (ticks % 20 == 0) {
            NucleoidSidebar.get(server).update();

            for (ServerWorld world : server.getWorlds()) {
                for (Entity entity : world.getEntitiesByType(EntityType.WITHER, entity -> true)) {
                    entity.remove();
                }
            }
        }
    }
}
