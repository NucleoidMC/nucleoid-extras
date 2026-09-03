package xyz.nucleoid.extras.util;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommonGuiElements {
    private static final GuiElement PURPLE_PLATE = new GuiElementBuilder(Items.PURPLE_STAINED_GLASS_PANE).setName(Text.empty()).build();
    private static final GuiElement WHITE_PLATE = new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE).setName(Text.empty()).build();
    public static GuiElementBuilder nextPage() {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
            .setName(Text.translatable("spectatorMenu.next_page").formatted(Formatting.WHITE))
            .hideFlags()
            .setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ");
    }

    public static GuiElementBuilder previousPage() {
        return new GuiElementBuilder(Items.PLAYER_HEAD)
            .setName(Text.translatable("spectatorMenu.previous_page").formatted(Formatting.WHITE))
            .hideFlags()
            .setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEwODI5OGZmMmIyNjk1MWQ2ODNlNWFkZTQ2YTQyZTkwYzJmN2M3ZGQ0MWJhYTkwOGJjNTg1MmY4YzMyZTU4MyJ9fX0");
    }

    public static GuiElementBuilder back(Runnable runnable) {
        return new GuiElementBuilder(Items.STRUCTURE_VOID).setName(ScreenTexts.BACK).setCallback((a, b, c, gui) -> {
            PagedGui.playClickSound(gui.getPlayer());
            runnable.run();
        });
    }

    public static GuiElement purple() {
        return PURPLE_PLATE;
    }

    public static GuiElement white() {
        return WHITE_PLATE;
    }
}
