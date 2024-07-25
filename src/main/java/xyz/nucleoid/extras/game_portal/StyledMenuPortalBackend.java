package xyz.nucleoid.extras.game_portal;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import xyz.nucleoid.extras.util.CommonGuiElements;
import xyz.nucleoid.extras.util.PagedGui;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.player.GamePlayerJoiner;
import xyz.nucleoid.plasmid.game.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.game.portal.GamePortalDisplay;
import xyz.nucleoid.plasmid.game.portal.menu.InvalidMenuEntry;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntry;

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
        var list = new ObjectOpenCustomHashSet<GameSpace>(Util.identityHashStrategy());
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
    public void applyTo(ServerPlayerEntity player) {
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
        var all = new GuiElementBuilder(Items.COMPASS);
        if (!viewOpen) {
            all.glow();
        } else {
            all.setCallback(() -> {
                this.fill(player, gui, false);
                PagedGui.playClickSound(player);
            });
        }

        gui.setSlot(5 * 9 + 3, all.setName(Text.translatable("nucleoid.navigator.all_games")));

        var open = new GuiElementBuilder(Items.REDSTONE_TORCH);
        if (viewOpen) {
            open.glow();
        } else {
            open.setCallback(() -> {
                this.fill(player, gui, true);
                PagedGui.playClickSound(player);
            });
        }
        gui.setSlot(5 * 9 + 5, open.setName(Text.translatable("nucleoid.navigator.open_games")));

        if (viewOpen) {
            this.fillOpen(player, gui, page);
        } else {
            this.fillInterface(player, gui, page);
        }
    }

    private void fillOpen(ServerPlayerEntity player, SimpleGui gui, MutableInt page) {
        var gamesTemp = new ObjectOpenCustomHashSet<GameSpace>(Util.identityHashStrategy());
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
                    var m = portal.getMetadata().sourceConfig();
                    gui.setSlot(index, createIconFor(m.icon(), m.name(), m.description(), portal.getPlayers().size(), (p) -> {
                        PagedGui.playClickSound(player);
                        tryJoinGame(p, portal);
                    }));
                } else {
                    gui.clearSlot(index);
                }
            }
        }
    }

    private static void tryJoinGame(ServerPlayerEntity player, GameSpace gameSpace) {
        player.server.submit(() -> {
            var results = GamePlayerJoiner.tryJoin(player, gameSpace);
            results.sendErrorsTo(player);
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

    protected GuiElementBuilder createIconFor(ItemStack icon, Text name, List<Text> description, int playerCount, Consumer<ServerPlayerEntity> click) {
            var element = GuiElementBuilder.from(icon).hideFlags()
                .setName(name);

        for (var line : description) {
            var text = line.copy();

            if (line.getStyle().getColor() == null) {
                text.setStyle(line.getStyle().withColor(Formatting.GRAY));
            }

            element.addLoreLine(text);
        }

        if (playerCount > -1) {
            element.addLoreLine(ScreenTexts.EMPTY);
            element.addLoreLine(Text.empty()
                    .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                    .append(Text.translatable("text.plasmid.ui.game_join.players",
                            Text.literal(playerCount + "").formatted(Formatting.YELLOW)).formatted(Formatting.GOLD))
            );
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
