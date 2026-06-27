package xyz.nucleoid.extras;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import xyz.nucleoid.extras.chat_filter.ChatFilter;
import xyz.nucleoid.extras.command.CommandAliases;
import xyz.nucleoid.extras.command.ExtraCommands;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;
import xyz.nucleoid.extras.event.NucleoidExtrasEvents;
import xyz.nucleoid.extras.game_portal.ExtrasGamePortals;
import xyz.nucleoid.extras.game_portal.ServerChangePortalBackend;
import xyz.nucleoid.extras.game_portal.entry.ExtraMenuEntries;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.extras.integrations.http.NucleoidHttpClient;
import xyz.nucleoid.extras.lobby.*;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;
import xyz.nucleoid.extras.network.NucleoidExtrasNetworking;
import xyz.nucleoid.extras.placeholder.ExtraPlaceholders;
import xyz.nucleoid.extras.resourcepack.GuiTextures;
import xyz.nucleoid.extras.scheduled_stop.ScheduledStop;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;

import java.net.URI;
import java.util.Calendar;

public final class NucleoidExtras implements ModInitializer {
    public static final String ID = "nucleoid_extras";
    public static final Logger LOGGER = LogManager.getLogger(NucleoidExtras.class);

    @Override
    public void onInitialize() {
        NEBlocks.register();
        NEDataComponentTypes.register();
        NEItems.register();
        NEEntities.register();
        NECriteria.register();

        ChatFilter.register();
        CommandAliases.register();
        ScheduledStop.register();

        NucleoidIntegrations.register();
        NucleoidHttpClient.register();
        ContributorData.register();

        ExtrasErrorReporter.register();
        ExtraPlaceholders.register();
        ExtrasGamePortals.register();
        ExtraMenuEntries.register();
        ExtraCommands.register();

        PlayerDataApi.register(PlayerLobbyState.STORAGE);

        NucleoidExtrasEvents.END_SERVER_TICK.register(NucleoidExtras::onServerTick);
        ServerLifecycleEvents.SERVER_STOPPED.register(NucleoidExtras::onServerStopped);
        ServerPlayConnectionEvents.JOIN.register(NucleoidExtras::onPlayerJoin);
        NucleoidExtrasNetworking.register();

        GuiTextures.register();
        if (PolymerResourcePackUtils.addModAssets(ID)) {
            LOGGER.info("Successfully added mod assets for " + ID);
        } else {
            LOGGER.error("Failed to add mod assets for " + ID);
        }

        //new DuckFixerUpper().onInitialize();
    }

    private static void onServerStopped(MinecraftServer server) {
        if (!server.isDedicatedServer()) {
            return;
        }

        var thread = new Thread(() -> {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                // Ignored
            }

            LOGGER.warn("Server is still running, even through it should shutdown!");
            for (var t : Thread.getAllStackTraces().keySet()) {
                if (!t.isDaemon()) {
                    LOGGER.warn("- {}", t.getName());
                }
            }
            try {
                Thread.sleep(5 * 1000);
                for (var t : Thread.getAllStackTraces().keySet()) {
                    t.interrupt();
                }
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                // Ignored
            }
            System.exit(1);
        }, "fallback shutdown");
        thread.setDaemon(true);
        thread.start();
    }

    private static void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        Calendar calendar = Calendar.getInstance();
        for (WrappedEvent event : NucleoidExtrasConfig.get().wrappedEvents()) {
            if (event.isDuring(calendar)) {
                handler.getPlayer().sendSystemMessage(
                    Component.translatable("text.nucleoid_extras.wrapped.join", event.year())
                        .withStyle(ChatFormatting.GREEN)
                        .withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(
                             URI.create("https://stats.nucleoid.xyz/players/" + handler.getPlayer().getStringUUID() + "/wrapped?year=" + event.year())
                        )))
                );
            }
        }
    }

    private static void onServerTick(MinecraftServer server) {
        int ticks = server.getTickCount();
        var config = NucleoidExtrasConfig.get();
        if (config.sidebar()) {
            NucleoidSidebar.get().update(ticks, server, config);
        }

        var integrations = NucleoidIntegrations.get();
        if (integrations != null) {
            integrations.tick();
        }

        ServerChangePortalBackend.tick(server);
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }
}
