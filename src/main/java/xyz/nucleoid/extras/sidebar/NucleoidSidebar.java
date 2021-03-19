package xyz.nucleoid.extras.sidebar;

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

public final class NucleoidSidebar {
    private static NucleoidSidebar instance;

    public static final RegistryKey<World> DIMENSION = World.OVERWORLD;

    private static final Text TITLE = new LiteralText("Nucleoid").formatted(Formatting.AQUA, Formatting.BOLD);

    private final SidebarWidget widget;

    private NucleoidSidebar(MinecraftServer server) {
        this.widget = new SidebarWidget(server, TITLE);
    }

    public static NucleoidSidebar get(MinecraftServer server) {
        if (instance == null) {
            instance = new NucleoidSidebar(server);
        }
        return instance;
    }

    public void update() {
        this.widget.set(this::writeSidebar);
    }

    private void writeSidebar(SidebarWidget.Content content) {
        content.writeFormattedTranslated(Formatting.GOLD, "nucleoid.sidebar.welcome");

        content.writeFormattedTranslated(Formatting.AQUA, "nucleoid.discord");

        Collection<ManagedGameSpace> openGames = ManagedGameSpace.getOpen();
        if (!openGames.isEmpty()) {
            content.writeLine("");
            this.writeGamesToSidebar(content, openGames);
        }
    }

    private void writeGamesToSidebar(SidebarWidget.Content content, Collection<ManagedGameSpace> openGames) {
        content.writeFormattedTranslated(Formatting.GOLD, "nucleoid.sidebar.games");

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
        content.writeFormattedTranslated(Formatting.GRAY, "nucleoid.sidebar.join");
        content.writeFormattedTranslated(Formatting.GRAY, "nucleoid.sidebar.compass");
    }

    public void addPlayer(ServerPlayerEntity player) {
        this.widget.addPlayer(player);
    }

    public void removePlayer(ServerPlayerEntity player) {
        this.widget.removePlayer(player);
    }
}
