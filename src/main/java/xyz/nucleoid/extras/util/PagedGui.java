package xyz.nucleoid.extras.util;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilderCreator;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.util.PlayerUtil;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public abstract class PagedGui extends SimpleGui {
    private static final Object2IntMap<MenuType<?>> TYPE_TO_SIZE = new Object2IntOpenHashMap<>();

    protected int page = 0;

    public static SimpleGui of(ServerPlayer player, List<GuiElement> elements) {
        return of(player, elements, null);
    }
    public static SimpleGui of(ServerPlayer player, List<GuiElement> elements, @Nullable IntFunction<GuiElement> navbar) {
        return new FromList(MenuType.GENERIC_9x6, player, false, elements, navbar);
    }

    public PagedGui(MenuType<?> type, ServerPlayer player, boolean includePlayerInventorySlots) {
        super(type, player, includePlayerInventorySlots);
    }

    protected void setPage(int page) {
        this.page = Math.min(this.getPageAmount() - 1, Math.max(0, page));
        this.updateDisplay();
    }

    protected void nextPage() {
        setPage(this.page + 1);
    }

    protected boolean canNextPage() {
        return this.getPageAmount() > this.page + 1;
    }

    protected void previousPage() {
        setPage(this.page - 1);
    }

    protected boolean canPreviousPage() {
        return this.page - 1 >= 0;
    }

    protected void updateDisplay() {
        var pageSize = this.getSinglePageSize();
        var offset = this.page * pageSize;

        for (int i = 0; i < pageSize; i++) {
            var element = this.getElement(offset + i);

            if (element == null) {
                element = DisplayElement.empty();
            }

            if (element.element() != null) {
                this.setSlot(i, element.element().get());
            } else if (element.slot() != null) {
                this.setSlot(i, element.slot());
            }
        }

        for (int i = 0; i < 9; i++) {
            var navElement = this.getNavElement(i);

            if (navElement == null) {
                navElement = DisplayElement.EMPTY;
            }

            if (navElement.element != null) {
                this.setSlot(i + pageSize, navElement.element.get());
            } else if (navElement.slot != null) {
                this.setSlot(i + pageSize, navElement.slot);
            }
        }
    }

    protected int getPage() {
        return this.page;
    }

    public final int getSinglePageSize() {
        return 9 * (TYPE_TO_SIZE.getInt(this.type) + (this.isIncludingPlayer() ? 4 : 0));
    }

    protected abstract int getPageAmount();

    protected abstract DisplayElement getElement(int id);

    protected DisplayElement getNavElement(int id) {
        return switch (id) {
            case 2 -> DisplayElement.previousPage(this);
            case 6 -> DisplayElement.nextPage(this);
            default -> filler();
        };
    }

    protected DisplayElement filler() {
        return DisplayElement.filler();
    }

    public record DisplayElement(@Nullable Supplier<GuiElement> element, @Nullable Slot slot) {
        private static final DisplayElement EMPTY = DisplayElement.of(new SimpleGuiElement(ItemStack.EMPTY, SimpleGuiElement.EMPTY_CALLBACK));
        private static final DisplayElement FILLER = DisplayElement.of(CommonGuiElements.white());

        public static DisplayElement of(Supplier<GuiElement> element) {
            return new DisplayElement(element, null);
        }
        public static DisplayElement of(GuiElement element) {
            return new DisplayElement(() -> element, null);
        }

        public static DisplayElement of(GuiElementBuilderCreator<?> element) {
            return new DisplayElement(element::build, null);
        }

        public static DisplayElement of(Slot slot) {
            return new DisplayElement(null, slot);
        }

        public static DisplayElement nextPage(PagedGui gui) {
            if (gui.canNextPage()) {
                return DisplayElement.of(
                    CommonGuiElements.nextPage(gui.player).setCallback(() -> {
                        playClickSound(gui.player);
                        gui.nextPage();
                    })
                );
            } else {
                /*return DisplayElement.of(
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setItemName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.DARK_GRAY))
                        .hideDefaultTooltip()
                        .setSkullOwner(SkinEncoder.encode("7e57720a4878c8bcab0e9c9c47d9e55128ccd77ba3445a54a91e3e1e1a27356e"))
                );*/
                return DisplayElement.empty();
            }
        }

        public static DisplayElement previousPage(PagedGui gui) {
            if (gui.canPreviousPage()) {
                return DisplayElement.of(
                    CommonGuiElements.previousPage(gui.player)
                        .setCallback(() -> {
                            playClickSound(gui.player);
                            gui.previousPage();
                        })
                );
            } else {
                /*return DisplayElement.of(
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setItemName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.DARK_GRAY))
                        .hideDefaultTooltip()
                        .setSkullOwner(SkinEncoder.encode("50820f76e3e041c75f76d0f301232bdf48321b534fe6a859ccb873d2981a9623"))
                );*/
                return DisplayElement.empty();
            }
        }

        public static DisplayElement filler() {
            return FILLER;
        }

        public static DisplayElement empty() {
            return EMPTY;
        }
    }

    public static void playSound(ServerPlayer player, SoundEvent sound) {
        PlayerUtil.playSoundToPlayer(player, sound, SoundSource.UI, 1, 1);
    }

    public static void playClickSound(ServerPlayer player) {
        playSound(player, SoundEvents.UI_BUTTON_CLICK.value());
    }

    public static class FromList extends PagedGui {

        protected final List<GuiElement> list;
        @Nullable
        private final IntFunction<GuiElement> navbar;

        public FromList(MenuType<?> type, ServerPlayer player, boolean includePlayerInventorySlots, List<GuiElement> guiElementInterfaces, IntFunction<GuiElement> navbar) {
            super(type, player, includePlayerInventorySlots);
            this.list = guiElementInterfaces;
            this.navbar = navbar;
            this.updateDisplay();
        }

        @Override
        protected DisplayElement getNavElement(int id) {
            var x = navbar != null ? navbar.apply(id) : null;
            return x != null ? DisplayElement.of(x) : super.getNavElement(id);
        }

        protected List<GuiElement> getList() {
            return list;
        }

        @Override
        protected int getPageAmount() {
            return this.getList().size() / this.getSinglePageSize() + 1;
        }

        @Override
        protected DisplayElement getElement(int id) {
            return this.getList().size() > id ? DisplayElement.of(this.getList().get(id)) : DisplayElement.empty();
        }
    }

    static {
        TYPE_TO_SIZE.defaultReturnValue(0);
        TYPE_TO_SIZE.put(MenuType.GENERIC_9x2, 1);
        TYPE_TO_SIZE.put(MenuType.GENERIC_9x3, 2);
        TYPE_TO_SIZE.put(MenuType.GENERIC_9x4, 3);
        TYPE_TO_SIZE.put(MenuType.GENERIC_9x5, 4);
        TYPE_TO_SIZE.put(MenuType.GENERIC_9x6, 5);
    }
}
