package xyz.nucleoid.extras.game_portal;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import xyz.nucleoid.extras.util.CommonGuiElements;
import xyz.nucleoid.extras.util.PagedGui;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceState;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.player.GamePlayerJoiner;
import xyz.nucleoid.plasmid.api.game.player.JoinIntent;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.GamePortalDisplay;
import xyz.nucleoid.plasmid.impl.portal.menu.InvalidMenuEntry;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public abstract class StyledMenuPortalBackend implements GamePortalBackend {
    public static final int GAMES_WIDTH = 7;
    public static final int GAMES_HEIGHT = 5;
    public static final int GAMES_X = 1;
    public static final int GAMES_Y = 0;
    public static final int GAMES_PER_PAGE = GAMES_HEIGHT * GAMES_WIDTH;

    private final Text name;
    private final MutableText hologramName;

    private final List<Text> description;
    private final ItemStack icon;
    private final Text uiTitle;

    public StyledMenuPortalBackend(Text name, Text uiTitle, List<Text> description, ItemStack icon) {
        this.name = name;
        this.uiTitle = uiTitle;
        var hologramName = name.copy();

        if (hologramName.getStyle().getColor() == null) {
            hologramName.setStyle(hologramName.getStyle().withColor(Formatting.AQUA));
        }

        this.hologramName = hologramName;
        this.description = description;
        this.icon = icon;

    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public List<Text> getDescription() {
        return this.description;
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public int getPlayerCount() {
        int count = 0;
        var list = new ReferenceOpenHashSet<GameSpace>();
        provideGameSpaces(list::add);
        for (var entry : list) {
            count += Math.max(0, entry.getPlayers().size());
        }
        return count;
    }

    @Override
    public void provideGameSpaces(Consumer<GameSpace> consumer) {
        for (var entry : this.getEntries()) {
            entry.provideGameSpaces(consumer);
        }
    }

    protected abstract List<MenuEntry> getEntries();

    @Override
    public void populateDisplay(GamePortalDisplay display) {
        display.set(GamePortalDisplay.NAME, this.hologramName);
        display.set(GamePortalDisplay.PLAYER_COUNT, this.getPlayerCount());
    }

    @Override
    public void applyTo(ServerPlayerEntity player, boolean alt) {
        var oldGui = GuiHelpers.getCurrentGui(player);

        var gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, player, false);
        gui.setTitle(this.uiTitle);
        var filler = CommonGuiElements.purple();

        for (int i = 0; i < 9; i++) {
            gui.setSlot(5 * 9 + i, filler);
        }

        if (oldGui != null) {
            gui.setSlot(5 * 9 + 8, CommonGuiElements.back(oldGui::open));
        }
        this.fill(player, gui, false);

        gui.open();
    }

    private void fill(ServerPlayerEntity player, SimpleGui gui, boolean viewOpen) {
        var page = new MutableInt();
        var filter = new GuiElementBuilder(viewOpen ? Items.SOUL_LANTERN : Items.LANTERN);
        filter.setCallback(() -> {
            this.fill(player, gui, !viewOpen);
            PagedGui.playClickSound(player);
        });

        if (viewOpen) {
            filter.glow();
            gui.setTitle(this.uiTitle.copy().append(Text.of(" ")).append(Text.translatable("nucleoid.navigator.open_only")));
            this.fillOpen(player, gui, page);
        } else {
            gui.setTitle(this.uiTitle);
            this.fillInterface(player, gui, page);
        }
        gui.setSlot(5 * 9 + 4, filter.setItemName(Text.translatable(viewOpen ? "nucleoid.navigator.open_games" : "nucleoid.navigator.all_games")).hideDefaultTooltip());
    }

    private void fillOpen(ServerPlayerEntity player, SimpleGui gui, MutableInt page) {
        var gamesTemp = new ReferenceOpenHashSet<GameSpace>();
        this.provideGameSpaces(gamesTemp::add);
        var games = new ArrayList<>(gamesTemp);
        games.sort(Comparator.comparingInt(space -> -space.getPlayers().size()));

        var pages = MathHelper.ceilDiv(games.size(), GAMES_PER_PAGE);

        if (pages > 1) {
            if (page.getValue() == 0) {
                gui.setSlot(9 * 2, GuiElement.EMPTY);
            } else {
                gui.setSlot(9 * 2, CommonGuiElements.previousPage().setCallback(() -> {
                    page.decrement();
                    PagedGui.playClickSound(player);
                    this.fillOpen(player, gui, page);
                }));
            }

            if (page.getValue() == pages - 1) {
                gui.setSlot(9 * 2 + 8, GuiElement.EMPTY);
            } else {
                gui.setSlot(9 * 2 + 8, CommonGuiElements.nextPage().setCallback(() -> {
                    page.increment();
                    PagedGui.playClickSound(player);
                    this.fillOpen(player, gui, page);
                }));
            }
        } else {
            gui.setSlot(9 * 2, GuiElement.EMPTY);
            gui.setSlot(9 * 2 + 8, GuiElement.EMPTY);
        }

        for (int y = 0; y < GAMES_HEIGHT; y++) {
            for (int x = 0; x < GAMES_WIDTH; x++) {
                var index = x + (y + GAMES_Y) * 9 + GAMES_X;
                var pIndex = x + y * GAMES_WIDTH + page.getValue() * GAMES_PER_PAGE;

                if (games.size() > pIndex) {
                    var portal = games.get(pIndex);
                    var m = portal.getMetadata().sourceConfig().value();
                    gui.setSlot(index, createIconFor(m.icon(), GameConfig.name(portal.getMetadata().sourceConfig()), m.description(), portal.getState(), (p) -> {
                        PagedGui.playClickSound(player);
                        tryJoinGame(p, portal, JoinIntent.PLAY);
                    }));
                } else {
                    gui.clearSlot(index);
                }
            }
        }
    }

    private static void tryJoinGame(ServerPlayerEntity player, GameSpace gameSpace, JoinIntent intent) {
        player.server.submit(() -> {
            var result = GamePlayerJoiner.tryJoin(player, gameSpace, intent);
            if (result.isError()) {
                player.sendMessage(result.errorCopy().formatted(Formatting.RED));
            }
        });
    }

    protected void fillInterface(ServerPlayerEntity player, SimpleGui gui, MutableInt page) {
        var entries = this.getEntries();
        var pages = MathHelper.ceilDiv(entries.size(), GAMES_PER_PAGE);

        if (pages > 1) {
            if (page.getValue() == 0) {
                gui.setSlot(9 * 2, GuiElement.EMPTY);
            } else {
                gui.setSlot(9 * 2, CommonGuiElements.previousPage().setCallback(() -> {
                    page.decrement();
                    PagedGui.playClickSound(player);
                    this.fillInterface(player, gui, page);
                }));
            }

            if (page.getValue() == pages - 1) {
                gui.setSlot(9 * 2 + 8, GuiElement.EMPTY);
            } else {
                gui.setSlot(9 * 2 + 8, CommonGuiElements.nextPage().setCallback(() -> {
                    page.increment();
                    PagedGui.playClickSound(player);
                    this.fillInterface(player, gui, page);
                }));
            }
        } else {
            gui.setSlot(9 * 2, GuiElement.EMPTY);
            gui.setSlot(9 * 2 + 8, GuiElement.EMPTY);
        }

        int pageOffset = page.getValue() * GAMES_PER_PAGE;
        var size = entries.size();

        var lastNonShift = (size / GAMES_WIDTH) * GAMES_WIDTH;
        var shift = (GAMES_WIDTH - (size - lastNonShift)) / 2;

        for (int y = 0; y < GAMES_HEIGHT; y++) {
            for (int x = 0; x < GAMES_WIDTH; x++) {
                gui.clearSlot(x + (y + GAMES_Y) * 9 + GAMES_X);
            }
        }
        var lastPage = pages - 1 == page.getValue();
        var starterY = lastPage ? Math.floorDiv(GAMES_HEIGHT - MathHelper.ceilDiv(size - ((size / GAMES_PER_PAGE) * GAMES_PER_PAGE), GAMES_WIDTH), 2) : 0;

        for (int y = starterY; y < GAMES_HEIGHT; y++) {
            for (int x = 0; x < GAMES_WIDTH; x++) {
                var index = x + (y + GAMES_Y) * 9 + GAMES_X;
                var pIndex = x + (y - starterY) * GAMES_WIDTH + pageOffset;

                if (lastNonShift > pIndex) {
                    var portal = entries.get(pIndex);
                    gui.setSlot(index, portal.createGuiElement());
                } else if (size > pIndex) {
                    var portal = entries.get(pIndex);
                    gui.setSlot(shift + index, portal.createGuiElement());
                }
            }
        }
    }

    protected GuiElementBuilder createIconFor(ItemStack icon, Text name, List<Text> description, GameSpaceState state, Consumer<ServerPlayerEntity> click) {
            var element = GuiElementBuilder.from(icon)
                .setItemName(name)
                .hideDefaultTooltip();

        for (var line : description) {
            var text = line.copy();

            if (line.getStyle().getColor() == null) {
                text.setStyle(line.getStyle().withColor(Formatting.GRAY));
            }

            element.addLoreLine(text);
        }

        int playerCount = state.players();
        int maxPlayerCount = state.maxPlayers();
        int spectatorCount = state.spectators();
        boolean allowSpace = true;
        var stateText = state.state().display();
        if (stateText != null) {
            element.addLoreLine(ScreenTexts.EMPTY);
            element.addLoreLine(Text.literal(" ").append(stateText).formatted(Formatting.WHITE));
            allowSpace = false;
        }

        if (playerCount > -1) {
            if (allowSpace) {
                element.addLoreLine(ScreenTexts.EMPTY);
                allowSpace = false;
            }

            element.addLoreLine(Text.empty().append(Text.literal("» ").formatted(Formatting.DARK_GRAY)).append(Text.translatable("text.plasmid.ui.game_join.players", new Object[]{Text.literal("" + playerCount + (maxPlayerCount > 0 ? " / " + maxPlayerCount : "")).formatted(Formatting.YELLOW)}).formatted(Formatting.GOLD)));
        }

        if (spectatorCount > 0) {
            if (allowSpace) {
                element.addLoreLine(ScreenTexts.EMPTY);
                allowSpace = false;
            }

            element.addLoreLine(Text.empty().append(Text.literal("» ").formatted(Formatting.DARK_GRAY)).append(Text.translatable("text.plasmid.ui.game_join.spectators", new Object[]{Text.literal("" + spectatorCount).formatted(Formatting.YELLOW)}).formatted(Formatting.GOLD)));
        }


        element.setCallback((a, b, c, gui) -> {
            click.accept(gui.getPlayer());
        });

        return element;
    }

    protected boolean canShow(MenuEntry a) {
        return ExtrasGamePortals.SHOW_INVALID || !(a instanceof InvalidMenuEntry);
    }
}
