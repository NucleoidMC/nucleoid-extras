package xyz.nucleoid.extras.lobby.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.util.PagedGui;

import java.util.List;

public class TaterBoxGui extends PagedGui.FromList {
	protected static final Text SHOW_UNFOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.show_unfound");
	protected static final Text HIDE_UNFOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.hide_unfound");
	protected static final Item UNFOUND_BUTTON_ICON = Items.POISONOUS_POTATO;

	protected static final Text COLLECT_ALL_TEXT = Text.translatable("text.nucleoid_extras.creative_tater_box.collect_all");
    protected static final Item COLLECT_ALL_ICON = Items.EMERALD;

	protected static final Text RESET_TEXT = Text.translatable("text.nucleoid_extras.creative_tater_box.reset");
    protected static final Item RESET_ICON = Items.CAMPFIRE;

	private final boolean creative;

	protected boolean hideUnfound = true;

	public TaterBoxGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, List<GuiElementInterface> guiElementInterfaces, boolean creative) {
		super(type, player, includePlayerInventorySlots, guiElementInterfaces, null);

		this.creative = creative;
	}

	public static TaterBoxGui of(ServerPlayerEntity player, List<GuiElementInterface> elements, boolean creative) {
		return new TaterBoxGui(ScreenHandlerType.GENERIC_9X6, player, false, elements, creative);
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
	public List<GuiElementInterface> getList() {
		List<GuiElementInterface> all = super.getList();

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
				.setCallback((x, y, z) -> {
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
                    TaterBoxItem.getCollectableTaters(player.getRegistryManager()).forEach(state.collectedTaters::add);
                }

                playSound(gui.player, SoundEvents.ENTITY_PLAYER_LEVELUP);
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

                playSound(gui.player, SoundEvents.ITEM_FIRECHARGE_USE);
                gui.close();
            });

        return DisplayElement.of(builder);
    }

	public static class TaterGuiElement extends GuiElement {
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

	public static class TaterGuiElementBuilder extends GuiElementBuilder {
		protected static final Text NOT_FOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.not_found").formatted(Formatting.RED);
		protected static final Item UNFOUND_ICON = Items.POTATO;

		protected boolean found;
		protected boolean collectable;

		public TaterGuiElementBuilder(Item item) {
			super(item);
		}

		public TaterGuiElementBuilder setFound(boolean found) {
			this.found = found;
			if(!found) {
				setItemName(NOT_FOUND_TEXT);
				setItem(UNFOUND_ICON);
			}
			return this;
		}

		public TaterGuiElementBuilder setCollectable(boolean collectable) {
			this.collectable = collectable;
			return this;
		}

		@Override
		public TaterGuiElement build() {
			return new TaterGuiElement(asStack(), callback, found, collectable);
		}
	}
}
