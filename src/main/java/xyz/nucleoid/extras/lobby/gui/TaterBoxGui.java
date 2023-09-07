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
import xyz.nucleoid.extras.util.PagedGui;

import java.util.List;

public class TaterBoxGui extends PagedGui.FromList {
	protected static final Text SHOW_UNFOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.show_unfound").formatted(Formatting.WHITE);
	protected static final Text HIDE_UNFOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.hide_unfound").formatted(Formatting.WHITE);
	protected static final Item UNFOUND_BUTTON_ICON = Items.POISONOUS_POTATO;

	protected static final Text COLLECT_ALL_TEXT = Text.translatable("text.nucleoid_extras.creative_tater_box.collect_all").formatted(Formatting.WHITE);
    protected static final Item COLLECT_ALL_ICON = Items.EMERALD;

	protected static final Text RESET_TEXT = Text.translatable("text.nucleoid_extras.creative_tater_box.reset").formatted(Formatting.WHITE);
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

		if(this.shouldHideUnfound()) {
			return all.stream().filter(element -> {
				if(element instanceof TaterGuiElement taterGuiElement) return taterGuiElement.isFound();
				else return true;
			}).toList();
		} else return all;
	}

	public static DisplayElement hideUnfoundButton(TaterBoxGui gui) {
		boolean hideUnfound = gui.shouldHideUnfound();

		GuiElementBuilder builder = new GuiElementBuilder(UNFOUND_BUTTON_ICON)
				.setName(hideUnfound ? SHOW_UNFOUND_TEXT : HIDE_UNFOUND_TEXT)
				.hideFlags()
				.setCallback((x, y, z) -> {
					playClickSound(gui.player);
					gui.toggleHideUnfound();
				});
		if(!hideUnfound) builder.glow();

		return DisplayElement.of(builder);
	}

	public static DisplayElement collectAllButton(TaterBoxGui gui) {
        GuiElementBuilder builder = new GuiElementBuilder(COLLECT_ALL_ICON)
            .setName(COLLECT_ALL_TEXT)
            .hideFlags()
            .setCallback(() -> {
                var state = PlayerLobbyState.get(gui.getPlayer());
                state.collectedTaters.addAll(TinyPotatoBlock.TATERS);

                playSound(gui.player, SoundEvents.ENTITY_PLAYER_LEVELUP);
                gui.close();
            });

        return DisplayElement.of(builder);
    }

	public static DisplayElement resetButton(TaterBoxGui gui) {
        GuiElementBuilder builder = new GuiElementBuilder(RESET_ICON)
            .setName(RESET_TEXT)
            .hideFlags()
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

		public TaterGuiElement(ItemStack item, ClickCallback callback, boolean found) {
			super(item, callback);
			this.found = found;
		}

		public boolean isFound() {
			return found;
		}
	}

	public static class TaterGuiElementBuilder extends GuiElementBuilder {
		protected static final Text NOT_FOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.not_found").formatted(Formatting.RED);
		protected static final Item UNFOUND_ICON = Items.POTATO;

		protected boolean found;

		public TaterGuiElementBuilder(Item item) {
			super(item);
		}

		public TaterGuiElementBuilder setFound(boolean found) {
			this.found = found;
			if(!found) {
				setName(NOT_FOUND_TEXT);
				setItem(UNFOUND_ICON);
			}
			return this;
		}

		@Override
		public TaterGuiElement build() {
			return new TaterGuiElement(asStack(), callback, found);
		}
	}
}
