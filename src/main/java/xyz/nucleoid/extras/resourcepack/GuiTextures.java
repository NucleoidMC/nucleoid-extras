package xyz.nucleoid.extras.resourcepack;

import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderCreator;
import net.minecraft.network.chat.Component;

import java.util.function.Function;
import java.util.function.Supplier;

import static xyz.nucleoid.extras.NucleoidExtras.identifier;
import static xyz.nucleoid.extras.resourcepack.UiResourceCreator.background;
import static xyz.nucleoid.extras.resourcepack.UiResourceCreator.icon16;

public class GuiTextures {
    public static final Function<Component, Component> GAME_PORTAL_9X6 = background("game_portal_9x6");
    public static final Function<Component, Component> TATERBOX = background("taterbox", 16);
    public static final Supplier<GuiElementBuilder> CHECKMARK = icon16("checkmark");
    public static final Supplier<GuiElementBuilder> CROSSMARK = icon16("crossmark");
    public static final Supplier<GuiElementBuilder> EMPTY_BUILDER = icon16("empty");
    public static final Supplier<GuiElementBuilder> NEXT_BUTTON = icon16("next_page");
    public static final Supplier<GuiElementBuilder> PREVIOUS_BUTTON = icon16("previous_page");
    public static final Supplier<GuiElementBuilder> BACK_BUTTON = icon16("back");
    public static final GuiElementBuilderCreator<?> EMPTY = EMPTY_BUILDER.get().hideTooltip();
    public static final char SPACE_1 = UiResourceCreator.space(1);

    public static void register() {
        ResourcePackExtras.forDefault().addBridgedModelsFolder(identifier("sgui"));
        UiResourceCreator.setup();
    }

}
