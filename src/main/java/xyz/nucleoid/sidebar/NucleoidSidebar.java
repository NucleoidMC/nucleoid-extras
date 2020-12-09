package xyz.nucleoid.sidebar;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

public final class NucleoidSidebar implements ModInitializer {
    public static final String ID = "nucleoid_sidebar";

    // TODO: make this configurable
    public static final RegistryKey<World> DIMENSION = World.OVERWORLD;

    private static final Text TITLE = new LiteralText("Nucleoid").formatted(Formatting.AQUA, Formatting.BOLD);

    private static SidebarWidget widget;

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(NucleoidSidebar::onServerTick);
    }

    private static SidebarWidget getWidget(MinecraftServer server) {
        if (widget == null) {
            widget = new SidebarWidget(server, TITLE);
        }
        return widget;
    }

    private static void onServerTick(MinecraftServer server) {
        int ticks = server.getTicks();
        if (ticks % 20 != 0) {
            return;
        }

        SidebarWidget widget = getWidget(server);
        widget.set(NucleoidSidebar::writeSidebar);
    }

    private static void writeSidebar(SidebarWidget.Content content) {
        content.writeLine(Formatting.GOLD + "Welcome to Nucleoid!");

        Collection<ManagedGameSpace> openGames = ManagedGameSpace.getOpen();
        if (!openGames.isEmpty()) {
            content.writeLine("");
            writeGamesToSidebar(content, openGames);
        }
    }

    private static void writeGamesToSidebar(SidebarWidget.Content content, Collection<ManagedGameSpace> openGames) {
        content.writeLine(Formatting.GOLD + "Open games:");

        Stream<ManagedGameSpace> games = openGames.stream()
                .sorted(Comparator.comparingInt(GameSpace::getPlayerCount).reversed())
                .limit(3);

        games.forEach(game -> {
            String name = game.getGameConfig().getName();
            if (name.length() > 12) {
                name = name.substring(0, 11) + "..";
            }

            int players = game.getPlayerCount();
            content.writeLine(Formatting.GREEN + name + ": " + Formatting.AQUA + players + " players");
        });

        content.writeLine("");
        content.writeLine(Formatting.GRAY + "...run /game join");
        content.writeLine(Formatting.GRAY + "or use the compass!");
    }

    public static void addPlayer(ServerPlayerEntity player) {
        SidebarWidget widget = getWidget(player.server);
        widget.addPlayer(player);
    }

    public static void removePlayer(ServerPlayerEntity player) {
        SidebarWidget widget = getWidget(player.server);
        widget.removePlayer(player);
    }
}
