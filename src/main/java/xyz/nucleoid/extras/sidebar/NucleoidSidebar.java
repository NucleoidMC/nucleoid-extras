package xyz.nucleoid.extras.sidebar;

import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.lines.LineBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;
import xyz.nucleoid.plasmid.game.manager.ManagedGameSpace;

import java.util.*;

public final class NucleoidSidebar {
    private static NucleoidSidebar instance;

    public static final RegistryKey<World> DIMENSION = World.OVERWORLD;

    private static final Style MAIN_TITLE_STYLE = Style.EMPTY.withColor(0x800080).withBold(true);
    private static final Style FLASH_TITLE_STYLE = Style.EMPTY.withColor(0xffffff);
    private static final Style ALT_TITLE_STYLE = Style.EMPTY.withColor(0x00bf59).withBold(true);

    private static final Style TOP_SIDEBAR_STYLE = Style.EMPTY.withColor(0xfff173);
    private static final Style GAME_TITLE_STYLE = Style.EMPTY.withColor(0xffac12);
    private static final Style GAME_COUNT_STYLE = Style.EMPTY.withColor(0xcccccc);
    private static final Style LINK_STYLE = Style.EMPTY.withColor(0x94eeff);

    private static final String NAME = "Nucleoid";
    private static final Text NAME_APPEND = new LiteralText(".xyz").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false));

    private static final Text TITLE_MAIN = new LiteralText(NAME).setStyle(MAIN_TITLE_STYLE).append(NAME_APPEND);
    private static final Text TITLE_ALT = new LiteralText(NAME).setStyle(ALT_TITLE_STYLE).append(NAME_APPEND);

    private static final Text[] TITLE_ANIMATION_1 = createAnimatedTitle(NAME, NAME_APPEND, MAIN_TITLE_STYLE, FLASH_TITLE_STYLE, ALT_TITLE_STYLE);
    private static final Text[] TITLE_ANIMATION_2 = createAnimatedTitle(NAME, NAME_APPEND, ALT_TITLE_STYLE, FLASH_TITLE_STYLE, MAIN_TITLE_STYLE);
    private static final int TITLE_SIZE = NAME.length();

    private static final Text SIZE_FORCING_TEXT = new LiteralText(" ".repeat(34));

    private static Text[] createAnimatedTitle(String string, Text append, Style leftStyle, Style middleStyle, Style rightStyle) {
        List<Text> texts = new ArrayList<>();

        for (int x = 0; x < string.length(); x++) {
            texts.add(new LiteralText(x == 0 ? "" : string.substring(0, x)).setStyle(leftStyle).append(
                    new LiteralText(string.substring(x, x + 1)).setStyle(middleStyle))
                    .append(new LiteralText(string.substring(x + 1)).setStyle(rightStyle))
                    .append(append)
            );
        }
        return texts.toArray(new Text[0]);
    }

    private final Sidebar widget;
    private boolean alt = false;

    private NucleoidSidebar() {
        this.widget = new Sidebar(TITLE_MAIN, Sidebar.Priority.LOW);
        this.widget.show();
    }

    public static NucleoidSidebar get() {
        if (instance == null) {
            instance = new NucleoidSidebar();
        }
        return instance;
    }

    public void update(long ticks, MinecraftServer server) {
        {
            int cycle = (int) ticks % 180 - 160;
            if (cycle == -160) {
                this.widget.setTitle(alt ? TITLE_MAIN : TITLE_ALT);
                alt = !alt;
            } else if (cycle > 0 && cycle % 2 == 0) {
                this.widget.setTitle((alt ? TITLE_ANIMATION_1 : TITLE_ANIMATION_2)[cycle * TITLE_SIZE / 20]);
            }
        }

        this.widget.set((b) -> {
            boolean altText = ticks % 120 < 60;
            b.add(SIZE_FORCING_TEXT);
            b.add((p) -> {
                if (p != null) {
                    return new LiteralText("» ").append(
                            new TranslatableText("nucleoid.sidebar.welcome",
                                    new LiteralText("").formatted(Formatting.WHITE).append(p.getDisplayName())
                            ).setStyle(TOP_SIDEBAR_STYLE)
                    ).formatted(Formatting.GRAY);
                } else {
                    return LiteralText.EMPTY;
                }
            });

            int playerCount = server.getCurrentPlayerCount();
            b.add(new LiteralText("» ").append(
                        new TranslatableText("nucleoid.sidebar.player_in_game." + (playerCount < 2 ? "1" : "more"),
                                new LiteralText("" + playerCount).formatted(Formatting.WHITE)
                        ).setStyle(TOP_SIDEBAR_STYLE)
            ).formatted(Formatting.GRAY));

            b.add(LiteralText.EMPTY);

            var openGames = GameSpaceManager.get().getOpenGameSpaces();
            if (!openGames.isEmpty()) {
                this.writeGamesToSidebar(b, openGames, altText);
            } else {
                b.add(new TranslatableText("nucleoid.sidebar.game.title.no_games").setStyle(GAME_TITLE_STYLE));
            }

            b.add(LiteralText.EMPTY);
            if (altText) {
                b.add(new TranslatableText("nucleoid.sidebar.join.1", new LiteralText("/game join").formatted(Formatting.WHITE)).formatted(Formatting.GRAY));
            } else {
                b.add(new TranslatableText("nucleoid.sidebar.join.2").formatted(Formatting.GRAY));
            }

            b.add(LiteralText.EMPTY);

            b.add(new TranslatableText("nucleoid.discord").setStyle(LINK_STYLE));
        });
    }

    private void writeGamesToSidebar(LineBuilder builder, Collection<ManagedGameSpace> openGames, boolean altText) {

        builder.add(new TranslatableText("nucleoid.sidebar.game.title").setStyle(GAME_TITLE_STYLE));

        /*for (var game : Map.of("GameName1", 5, "DTM v2", 8, "Another One", 4, "Bed Wars", 7).entrySet()) {
            var name = game.getKey();
            if (name.length() > 12) {
                name = name.substring(0, 11) + "..";
            }

            int players = game.getValue();*/
        var games = openGames.stream()
                .sorted(Comparator.comparingInt(GameSpace::getPlayerCount).reversed())
                .limit(4);

        games.forEach(game -> {
            // This should be replaced with better shortened names (ideally predefined ones)
            var name = game.getSourceConfig().getName().asString();
            if (name.length() > 12) {
                name = name.substring(0, 11) + "..";
            }

            int players = game.getPlayerCount();
            var playersText = new TranslatableText("nucleoid.sidebar.game.player." + (players < 2 ? "1" : "more"), players).setStyle(GAME_COUNT_STYLE);

            builder.add(new LiteralText(" • ")
                    .formatted(Formatting.DARK_GRAY)
                    .append(new TranslatableText("nucleoid.sidebar.game.entry", name, playersText).formatted(Formatting.WHITE)));
        });

        if (openGames.size() > 4) {
            builder.add(new TranslatableText("nucleoid.sidebar.game.more", openGames.size() - 4).setStyle(GAME_COUNT_STYLE));
        }
    }

    public void addPlayer(ServerPlayerEntity player) {
        this.widget.addPlayer(player);
    }

    public void removePlayer(ServerPlayerEntity player) {
        this.widget.removePlayer(player);
    }
}
