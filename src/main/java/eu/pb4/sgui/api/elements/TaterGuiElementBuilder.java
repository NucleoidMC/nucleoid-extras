package eu.pb4.sgui.api.elements;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import xyz.nucleoid.extras.lobby.gui.TaterBoxGui;

public class TaterGuiElementBuilder extends BaseItemStackBuilder<TaterGuiElementBuilder>
    implements GuiElementBuilderCreator<TaterGuiElementBuilder> {
    protected static final Component NOT_FOUND_TEXT = Component.translatable("text.nucleoid_extras.tater_box.not_found").withStyle(ChatFormatting.RED);
    protected static final Item UNFOUND_ICON = Items.POTATO;

    protected boolean found;
    protected boolean collectable;
    private GuiElement.ClickCallback callback = SimpleGuiElement.EMPTY_CALLBACK;

    public TaterGuiElementBuilder(Item item) {
        super();
        this.item = item;
    }

    public TaterGuiElementBuilder setFound(boolean found) {
        this.found = found;
        if (!found) {
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
    public TaterGuiElementBuilder setCallback(GuiElement.ClickCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public TaterBoxGui.TaterGuiElement build() {
        return new TaterBoxGui.TaterGuiElement(asStack(), callback, found, collectable);
    }
}
