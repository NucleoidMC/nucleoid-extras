package xyz.nucleoid.extras.sidebar;

import eu.pb4.sidebars.api.Sidebar;
import eu.pb4.sidebars.api.lines.LineBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class NucleoidSidebar {
    private static NucleoidSidebar instance;

    public static final ResourceKey<Level> DIMENSION = Level.OVERWORLD;

    private static final Style MAIN_TITLE_STYLE = Style.EMPTY.withColor(0x800080).withBold(true);
    private static final Style FLASH_TITLE_STYLE = Style.EMPTY.withColor(0xffffff);
    private static final Style ALT_TITLE_STYLE = Style.EMPTY.withColor(0x00bf59).withBold(true);

    private static final Style TOP_SIDEBAR_STYLE = Style.EMPTY.withColor(0xfff173);
    private static final Style GAME_TITLE_STYLE = Style.EMPTY.withColor(0xffac12);
    private static final Style GAME_COUNT_STYLE = Style.EMPTY.withColor(0xcccccc);
    private static final Style LINK_STYLE = Style.EMPTY.withColor(0x94eeff);

    private static final String NAME = "Nucleoid";
    private static final Component DEV_TITLE = Component.literal(" (DEV)").setStyle(Style.EMPTY.withColor(0xbf0059));

    private static final Component NAME_APPEND = Component.literal(".xyz").setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(false));

    private static final Component TITLE_MAIN = Component.literal(NAME).setStyle(MAIN_TITLE_STYLE).append(NAME_APPEND);
    private static final Component TITLE_ALT = Component.literal(NAME).setStyle(ALT_TITLE_STYLE).append(NAME_APPEND);


    private static final Component[] TITLE_ANIMATION_1 = createAnimatedTitle(NAME, NAME_APPEND, MAIN_TITLE_STYLE, FLASH_TITLE_STYLE, ALT_TITLE_STYLE);
    private static final Component[] TITLE_ANIMATION_2 = createAnimatedTitle(NAME, NAME_APPEND, ALT_TITLE_STYLE, FLASH_TITLE_STYLE, MAIN_TITLE_STYLE);
    private static final int TITLE_SIZE = NAME.length();

    private static final Component SIZE_FORCING_TEXT = Component.literal(" ".repeat(34));

    private final boolean enabled = NucleoidExtrasConfig.get().sidebar();

    private static Component[] createAnimatedTitle(String string, Component append, Style leftStyle, Style middleStyle, Style rightStyle) {
        List<Component> texts = new ArrayList<>();

        for (int x = 0; x < string.length(); x++) {
            texts.add(Component.literal(x == 0 ? "" : string.substring(0, x)).setStyle(leftStyle).append(
                    Component.literal(string.substring(x, x + 1)).setStyle(middleStyle))
                    .append(Component.literal(string.substring(x + 1)).setStyle(rightStyle))
                    .append(append)
            );
        }
        return texts.toArray(new Component[0]);
    }

    private final Sidebar widget;
    private boolean alt = false;

    private NucleoidSidebar() {
        this.widget = new Sidebar(TITLE_MAIN, Sidebar.Priority.LOW);
        this.widget.setDefaultNumberFormat(BlankFormat.INSTANCE);
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
            Component title = null;
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
                    return Component.literal("» ").append(
                            Component.translatable("nucleoid.sidebar.welcome",
                                    Component.empty().withStyle(ChatFormatting.WHITE).append(p.getDisplayName())
                            ).setStyle(TOP_SIDEBAR_STYLE)
                    ).withStyle(ChatFormatting.GRAY);
                } else {
                    return Component.empty();
                }
            });

            int playerCount = server.getPlayerCount();
            b.add(Component.literal("» ").append(
                        Component.translatable("nucleoid.sidebar.player_in_game." + (playerCount < 2 ? "1" : "more"),
                                Component.literal("" + playerCount).withStyle(ChatFormatting.WHITE)
                        ).setStyle(TOP_SIDEBAR_STYLE)
            ).withStyle(ChatFormatting.GRAY));

            b.add(Component.empty());

            var openGames = GameSpaceManager.get().getOpenGameSpaces();
            if (!openGames.isEmpty()) {
                this.writeGamesToSidebar(b, openGames);
            } else {
                b.add(Component.translatable("nucleoid.sidebar.game.title.no_games").setStyle(GAME_TITLE_STYLE));
            }

            b.add(Component.empty());
            if (altText) {
                b.add(Component.translatable("nucleoid.sidebar.join.1", Component.literal("/game join").withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
            } else {
                b.add(Component.translatable("nucleoid.sidebar.join.2").withStyle(ChatFormatting.GRAY));
            }

            b.add(Component.empty());

            b.add(Component.translatable("nucleoid.discord").setStyle(LINK_STYLE));
        });
    }

    private void writeGamesToSidebar(LineBuilder builder, Collection<GameSpace> openGames) {
        builder.add(Component.translatable("nucleoid.sidebar.game.title").setStyle(GAME_TITLE_STYLE));

        var games = openGames.stream()
                .sorted(Comparator.comparingInt((GameSpace space) -> space.getPlayers().size()).reversed())
                .limit(4);

        games.forEach(game -> {
            var name = GameConfig.shortName(game.getMetadata().sourceConfig());

            int players = game.getPlayers().size();
            var playersText = Component.translatable("nucleoid.sidebar.game.player." + (players < 2 ? "1" : "more"), players).setStyle(GAME_COUNT_STYLE);

            builder.add(Component.literal(" • ")
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable("nucleoid.sidebar.game.entry", name, playersText).withStyle(ChatFormatting.WHITE)));
        });

        if (openGames.size() > 4) {
            builder.add(Component.translatable("nucleoid.sidebar.game.more", openGames.size() - 4).setStyle(GAME_COUNT_STYLE));
        }
    }

    public void addPlayer(ServerPlayer player) {
        if (this.enabled) {
            this.widget.addPlayer(player);
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.enabled) {
            this.widget.removePlayer(player);
        }
    }
}
