package xyz.nucleoid.extras.lobby.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.extras.util.PagedGui;

import java.util.List;

public class TaterBoxGui extends PagedGui.FromList {
	protected static final Text SHOW_UNFOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.show_unfound").formatted(Formatting.WHITE);
	protected static final Text HIDE_UNFOUND_TEXT = Text.translatable("text.nucleoid_extras.tater_box.hide_unfound").formatted(Formatting.WHITE);
	protected static final Item UNFOUND_BUTTON_ICON = Items.POISONOUS_POTATO;

	protected boolean hideUnfound = true;

	public TaterBoxGui(ScreenHandlerType<?> type, ServerPlayerEntity player, boolean includePlayerInventorySlots, List<GuiElementInterface> guiElementInterfaces) {
		super(type, player, includePlayerInventorySlots, guiElementInterfaces, null);
	}

	public static TaterBoxGui of(ServerPlayerEntity player, List<GuiElementInterface> elements) {
		return new TaterBoxGui(ScreenHandlerType.GENERIC_9X6, player, false, elements);
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
