package xyz.nucleoid.extras.sidebar;

import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.lines.LineBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;
import xyz.nucleoid.plasmid.game.manager.ManagedGameSpace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

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
    private static final Text DEV_TITLE = Text.literal(" (DEV)").setStyle(Style.EMPTY.withColor(0xbf0059));

    private static final Text NAME_APPEND = Text.literal(".xyz").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withBold(false));

    private static final Text TITLE_MAIN = Text.literal(NAME).setStyle(MAIN_TITLE_STYLE).append(NAME_APPEND);
    private static final Text TITLE_ALT = Text.literal(NAME).setStyle(ALT_TITLE_STYLE).append(NAME_APPEND);


    private static final Text[] TITLE_ANIMATION_1 = createAnimatedTitle(NAME, NAME_APPEND, MAIN_TITLE_STYLE, FLASH_TITLE_STYLE, ALT_TITLE_STYLE);
    private static final Text[] TITLE_ANIMATION_2 = createAnimatedTitle(NAME, NAME_APPEND, ALT_TITLE_STYLE, FLASH_TITLE_STYLE, MAIN_TITLE_STYLE);
    private static final int TITLE_SIZE = NAME.length();

    private static final Text SIZE_FORCING_TEXT = Text.literal(" ".repeat(34));

    private static Text[] createAnimatedTitle(String string, Text append, Style leftStyle, Style middleStyle, Style rightStyle) {
        List<Text> texts = new ArrayList<>();

        for (int x = 0; x < string.length(); x++) {
            texts.add(Text.literal(x == 0 ? "" : string.substring(0, x)).setStyle(leftStyle).append(
                    Text.literal(string.substring(x, x + 1)).setStyle(middleStyle))
                    .append(Text.literal(string.substring(x + 1)).setStyle(rightStyle))
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

    public void update(long ticks, MinecraftServer server, NucleoidExtrasConfig config) {
        {
            int cycle = (int) ticks % 180 - 160;
            Text title = null;
            if (cycle == -160) {
                title = alt ? TITLE_MAIN : TITLE_ALT;
                alt = !alt;
            } else if (cycle > 0 && cycle % 2 == 0) {
                title = (alt ? TITLE_ANIMATION_1 : TITLE_ANIMATION_2)[cycle * TITLE_SIZE / 20];
            }
            if (title != null) {
                if (config.devServer()) {
                    this.widget.setTitle(title.copy().append(DEV_TITLE));
                } else {
                    this.widget.setTitle(title);
                }
            }
        }

        this.widget.set((b) -> {
            boolean altText = ticks % 120 < 60;
            b.add(SIZE_FORCING_TEXT);
            b.add((p) -> {
                if (p != null) {
                    return Text.literal("» ").append(
                            Text.translatable("nucleoid.sidebar.welcome",
                                    Text.literal("").formatted(Formatting.WHITE).append(p.getDisplayName())
                            ).setStyle(TOP_SIDEBAR_STYLE)
                    ).formatted(Formatting.GRAY);
                } else {
                    return Text.empty();
                }
            });

            int playerCount = server.getCurrentPlayerCount();
            b.add(Text.literal("» ").append(
                        Text.translatable("nucleoid.sidebar.player_in_game." + (playerCount < 2 ? "1" : "more"),
                                Text.literal("" + playerCount).formatted(Formatting.WHITE)
                        ).setStyle(TOP_SIDEBAR_STYLE)
            ).formatted(Formatting.GRAY));

            b.add(Text.empty());

            var openGames = GameSpaceManager.get().getOpenGameSpaces();
            if (!openGames.isEmpty()) {
                this.writeGamesToSidebar(b, openGames);
            } else {
                b.add(Text.translatable("nucleoid.sidebar.game.title.no_games").setStyle(GAME_TITLE_STYLE));
            }

            b.add(Text.empty());
            if (altText) {
                b.add(Text.translatable("nucleoid.sidebar.join.1", Text.literal("/game join").formatted(Formatting.WHITE)).formatted(Formatting.GRAY));
            } else {
                b.add(Text.translatable("nucleoid.sidebar.join.2").formatted(Formatting.GRAY));
            }

            b.add(Text.empty());

            b.add(Text.translatable("nucleoid.discord").setStyle(LINK_STYLE));
        });
    }

    private void writeGamesToSidebar(LineBuilder builder, Collection<ManagedGameSpace> openGames) {
        builder.add(Text.translatable("nucleoid.sidebar.game.title").setStyle(GAME_TITLE_STYLE));

        var games = openGames.stream()
                .sorted(Comparator.comparingInt((ManagedGameSpace space) -> space.getPlayers().size()).reversed())
                .limit(4);

        games.forEach(game -> {
            var name = game.getMetadata().sourceConfig().shortName();

            int players = game.getPlayers().size();
            var playersText = Text.translatable("nucleoid.sidebar.game.player." + (players < 2 ? "1" : "more"), players).setStyle(GAME_COUNT_STYLE);

            builder.add(Text.literal(" • ")
                    .formatted(Formatting.DARK_GRAY)
                    .append(Text.translatable("nucleoid.sidebar.game.entry", name, playersText).formatted(Formatting.WHITE)));
        });

        if (openGames.size() > 4) {
            builder.add(Text.translatable("nucleoid.sidebar.game.more", openGames.size() - 4).setStyle(GAME_COUNT_STYLE));
        }
    }

    public void addPlayer(ServerPlayerEntity player) {
        this.widget.addPlayer(player);
    }

    public void removePlayer(ServerPlayerEntity player) {
        this.widget.removePlayer(player);
    }
}
