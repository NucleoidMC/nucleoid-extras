package xyz.nucleoid.extras.lobby.gui;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.SimpleGuiElement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.resourcepack.GuiTextures;
import xyz.nucleoid.extras.util.PagedGui;

import java.util.List;

public class TaterBoxGui extends PagedGui.FromList {
	protected static final Component SHOW_UNFOUND_TEXT = Component.translatable("text.nucleoid_extras.tater_box.show_unfound");
	protected static final Component HIDE_UNFOUND_TEXT = Component.translatable("text.nucleoid_extras.tater_box.hide_unfound");
	protected static final Item UNFOUND_BUTTON_ICON = Items.POISONOUS_POTATO;

	protected static final Component COLLECT_ALL_TEXT = Component.translatable("text.nucleoid_extras.creative_tater_box.collect_all");
    protected static final Item COLLECT_ALL_ICON = Items.EMERALD;

	protected static final Component RESET_TEXT = Component.translatable("text.nucleoid_extras.creative_tater_box.reset");
    protected static final Item RESET_ICON = Items.CAMPFIRE;

	private final boolean creative;

	protected boolean hideUnfound = true;

	public TaterBoxGui(MenuType<?> type, ServerPlayer player, boolean includePlayerInventorySlots, List<GuiElement> guiElementInterfaces, boolean creative) {
		super(type, player, includePlayerInventorySlots, guiElementInterfaces, null);

		this.creative = creative;
	}

	public static TaterBoxGui of(ServerPlayer player, List<GuiElement> elements, boolean creative) {
		return new TaterBoxGui(MenuType.GENERIC_9x6, player, false, elements, creative);
	}

	public boolean shouldHideUnfound() {
		return hideUnfound;
	}

	public void setHideUnfound(boolean hideUnfound) {
		this.hideUnfound = hideUnfound;
		this.updateDisplay();
		if(this.getPage() > this.getPageAmount()) {
			this.setPage(this.getPageAmount() - 1);
		}
	}

	public void toggleHideUnfound() {
		this.setHideUnfound(!shouldHideUnfound());
	}

	@Override
	protected DisplayElement getNavElement(int id) {
        if (creative) {
            if (id == 0) return TaterBoxGui.collectAllButton(this);
            if (id == 8) return TaterBoxGui.resetButton(this);
        }

		if(id == 4) {
			return TaterBoxGui.hideUnfoundButton(this);
		} else return super.getNavElement(id);
	}

	@Override
	public List<GuiElement> getList() {
		List<GuiElement> all = super.getList();

		return all.stream().filter(element -> {
			if (element instanceof TaterGuiElement taterGuiElement) {
				return taterGuiElement.shouldShow(this.shouldHideUnfound());
			}

			return true;
		}).toList();
	}

	public static DisplayElement hideUnfoundButton(TaterBoxGui gui) {
		boolean hideUnfound = gui.shouldHideUnfound();

		GuiElementBuilder builder = new GuiElementBuilder(UNFOUND_BUTTON_ICON)
				.setItemName(hideUnfound ? SHOW_UNFOUND_TEXT : HIDE_UNFOUND_TEXT)
				.hideDefaultTooltip()
				.setCallback(() -> {
					playClickSound(gui.player);
					gui.toggleHideUnfound();
				});
		if(!hideUnfound) builder.glow();

		return DisplayElement.of(builder);
	}

	public static DisplayElement collectAllButton(TaterBoxGui gui) {
        GuiElementBuilder builder = new GuiElementBuilder(COLLECT_ALL_ICON)
            .setItemName(COLLECT_ALL_TEXT)
            .hideDefaultTooltip()
            .setCallback(clickType -> {
                var player = gui.getPlayer();
                var state = PlayerLobbyState.get(gui.getPlayer());

                if (clickType.shift) {
                    state.collectedTaters.addAll(TinyPotatoBlock.TATERS);
                } else {
                    TaterBoxItem.getCollectableTaters(player.registryAccess()).forEach(state.collectedTaters::add);
                }

                playSound(gui.player, SoundEvents.PLAYER_LEVELUP);
                gui.close();
            });

        return DisplayElement.of(builder);
    }

	public static DisplayElement resetButton(TaterBoxGui gui) {
        GuiElementBuilder builder = new GuiElementBuilder(RESET_ICON)
            .setItemName(RESET_TEXT)
            .hideDefaultTooltip()
            .setCallback(() -> {
                var state = PlayerLobbyState.get(gui.getPlayer());
                state.collectedTaters.clear();

                playSound(gui.player, SoundEvents.FIRECHARGE_USE);
                gui.close();
            });

        return DisplayElement.of(builder);
    }

    @Override
    public void setTitle(Component title) {
        super.setTitle(PolymerResourcePackUtils.hasMainPack(this.player) ? GuiTextures.TATERBOX.apply(title) : title);
    }

    @Override
    protected DisplayElement filler() {
        if (PolymerResourcePackUtils.hasMainPack(this.player)) {
            return DisplayElement.empty();
        }
        return super.filler();
    }

    public static class TaterGuiElement extends SimpleGuiElement {
		protected final boolean found;
		protected final boolean collectable;

		public TaterGuiElement(ItemStack item, ClickCallback callback, boolean found, boolean collectable) {
			super(item, callback);
			this.found = found;
			this.collectable = collectable;
		}

		public boolean shouldShow(boolean hideUnfound) {
			if (this.found) {
				return true;
			}

			return !hideUnfound && this.collectable;
		}
	}
}
