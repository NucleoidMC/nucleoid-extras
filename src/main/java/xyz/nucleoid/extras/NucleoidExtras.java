package xyz.nucleoid.extras;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.extras.chat_filter.ChatFilter;
import xyz.nucleoid.extras.command.CommandAliases;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.extras.scheduled_stop.ScheduledStop;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;
import xyz.nucleoid.extras.statistics.NucleoidStatistics;

public final class NucleoidExtras implements ModInitializer {
    public static final String ID = "nucleoid_extras";
    public static final Logger LOGGER = LogManager.getLogger(NucleoidExtras.class);

    @Override
    public void onInitialize() {
        NEBlocks.register();
        NEItems.register();

        ChatFilter.register();
        CommandAliases.register();
        ScheduledStop.register();

        NucleoidIntegrations.register();

        ServerTickEvents.END_SERVER_TICK.register(NucleoidExtras::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        int ticks = server.getTicks();
        if (ticks % 20 == 0) {
            NucleoidExtrasConfig config = NucleoidExtrasConfig.get();
            if (config.isSidebarEnabled()) {
                NucleoidSidebar.get(server).update();
            }
        }

        NucleoidIntegrations integrations = NucleoidIntegrations.get();
        if (integrations != null) {
            integrations.tick();
        }
    }

    public static Identifier identifier(String path) {
        return new Identifier(ID, path);
    }
}
