package xyz.nucleoid.extras.util;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntFunction;

public abstract class PagedGui extends SimpleGui {
    private static final Object2IntMap<ScreenHandlerType<?>> TYPE_TO_SIZE = new Object2IntOpenHashMap<>();

    protected int page = 0;

    public static SimpleGui of(ServerPlayerEntity player, List<GuiElementInterface> elements) {
        return of(player, elements, null);
    }
    public static SimpleGui of(ServerPlayerEntity player, List<GuiElementInterface> elements, @Nullable IntFunction<GuiElementInterface> navbar) {
        return new FromList(ScreenHandlerType.GENERIC_9X6, player, false, elements, navbar);
    }

    public PagedGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots) {
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
                this.setSlot(i, element.element());
            } else if (element.slot() != null) {
                this.setSlotRedirect(i, element.slot());
            }
        }

        for (int i = 0; i < 9; i++) {
            var navElement = this.getNavElement(i);

            if (navElement == null) {
                navElement = DisplayElement.EMPTY;
            }

            if (navElement.element != null) {
                this.setSlot(i + pageSize, navElement.element);
            } else if (navElement.slot != null) {
                this.setSlotRedirect(i + pageSize, navElement.slot);
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
            default -> DisplayElement.filler();
        };
    }

    public record DisplayElement(@Nullable GuiElementInterface element, @Nullable Slot slot) {
        private static final DisplayElement EMPTY = DisplayElement.of(new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK));
        private static final DisplayElement FILLER = DisplayElement.of(
            new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE)
                .setName(Text.empty())
                .hideFlags()
        );

        public static DisplayElement of(GuiElementInterface element) {
            return new DisplayElement(element, null);
        }

        public static DisplayElement of(GuiElementBuilderInterface<?> element) {
            return new DisplayElement(element.build(), null);
        }

        public static DisplayElement of(Slot slot) {
            return new DisplayElement(null, slot);
        }

        public static DisplayElement nextPage(PagedGui gui) {
            if (gui.canNextPage()) {
                return DisplayElement.of(
                    CommonGuiElements.nextPage().setCallback((x, y, z) -> {
                        playClickSound(gui.player);
                        gui.nextPage();
                    })
                );
            } else {
                return DisplayElement.of(
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.DARK_GRAY))
                        .hideFlags()
                        .setSkullOwner(SkinEncoder.encode("7e57720a4878c8bcab0e9c9c47d9e55128ccd77ba3445a54a91e3e1e1a27356e"))
                );
            }
        }

        public static DisplayElement previousPage(PagedGui gui) {
            if (gui.canPreviousPage()) {
                return DisplayElement.of(
                    CommonGuiElements.previousPage()
                        .setCallback((x, y, z) -> {
                            playClickSound(gui.player);
                            gui.previousPage();
                        })
                );
            } else {
                return DisplayElement.of(
                    new GuiElementBuilder(Items.PLAYER_HEAD)
                        .setName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.DARK_GRAY))
                        .hideFlags()
                        .setSkullOwner(SkinEncoder.encode("50820f76e3e041c75f76d0f301232bdf48321b534fe6a859ccb873d2981a9623"))
                );
            }
        }

        public static DisplayElement filler() {
            return FILLER;
        }

        public static DisplayElement empty() {
            return EMPTY;
        }
    }

    public static void playClickSound(ServerPlayerEntity player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);
    }

    public static class FromList extends PagedGui {

        protected final List<GuiElementInterface> list;
        @Nullable
        private final IntFunction<GuiElementInterface> navbar;

        public FromList(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, List<GuiElementInterface> guiElementInterfaces, IntFunction<GuiElementInterface> navbar) {
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

        protected List<GuiElementInterface> getList() {
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
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X2, 1);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X3, 2);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X4, 3);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X5, 4);
        TYPE_TO_SIZE.put(ScreenHandlerType.GENERIC_9X6, 5);
    }
}
