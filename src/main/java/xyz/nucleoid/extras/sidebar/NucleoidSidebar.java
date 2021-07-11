package xyz.nucleoid.extras.sidebar;

import eu.pb4.sidebars.api.lines.LineBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.widget.SidebarWidget;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;
import xyz.nucleoid.plasmid.game.manager.ManagedGameSpace;

import java.util.Collection;
import java.util.Comparator;

public final class NucleoidSidebar {
    private static NucleoidSidebar instance;

    public static final RegistryKey<World> DIMENSION = World.OVERWORLD;

    private static final Text TITLE = new LiteralText("Nucleoid").formatted(Formatting.AQUA, Formatting.BOLD);

    private final SidebarWidget widget;

    private NucleoidSidebar() {
        this.widget = new SidebarWidget(TITLE);
    }

    public static NucleoidSidebar get() {
        if (instance == null) {
            instance = new NucleoidSidebar();
        }
        return instance;
    }

    public void update() {
        this.widget.set(this::writeSidebar);
    }

    private void writeSidebar(LineBuilder builder) {
        builder.add(new TranslatableText("nucleoid.sidebar.welcome").formatted(Formatting.GOLD));

        builder.add(new TranslatableText("nucleoid.discord").formatted(Formatting.AQUA));

        var openGames = GameSpaceManager.get().getOpenGameSpaces();
        if (!openGames.isEmpty()) {
            builder.add(new LiteralText(""));
            this.writeGamesToSidebar(builder, openGames);
        }
    }

    private void writeGamesToSidebar(LineBuilder builder, Collection<ManagedGameSpace> openGames) {
        builder.add(new TranslatableText("nucleoid.sidebar.games").formatted(Formatting.GOLD));

        var games = openGames.stream()
                .sorted(Comparator.comparingInt(GameSpace::getPlayerCount).reversed())
                .limit(3);

        games.forEach(game -> {
            var name = game.getSourceConfig().getName().asString();
            if (name.length() > 12) {
                name = name.substring(0, 11) + "..";
            }

            int players = game.getPlayerCount();
            var playersText = new TranslatableText("nucleoid.sidebar.game.player." + (players < 2 ? "1" : "more"), players);

            builder.add(new TranslatableText("nucleoid.sidebar.game", name, playersText.formatted(Formatting.AQUA)));
        });

        builder.add(new LiteralText(""));
        builder.add(new TranslatableText("nucleoid.sidebar.join").formatted(Formatting.GRAY));
        builder.add(new TranslatableText("nucleoid.sidebar.compass").formatted(Formatting.GRAY));
    }

    public void addPlayer(ServerPlayerEntity player) {
        this.widget.addPlayer(player);
    }

    public void removePlayer(ServerPlayerEntity player) {
        this.widget.removePlayer(player);
    }
}
