package xyz.nucleoid.extras.game_portal.entry;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.impl.portal.GamePortal;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntry;

import java.util.List;
import java.util.function.Consumer;

public record QuickPortalEntry(
    GamePortal portal,
    GamePortal quickPortal,
    Text message,
    Text name,
    List<Text> description,
    ItemStack icon
) implements MenuEntry {
    @Override
    public void click(ServerPlayerEntity player, boolean alt) {
        this.quickPortal.requestJoin(player, alt);
    }

    public void secondaryClick(ServerPlayerEntity player) {
        this.portal.requestJoin(player, false);
    }

    @Override
    public int getPlayerCount() {
        return this.portal.getPlayerCount();
    }

    @Override
    public void provideGameSpaces(Consumer<GameSpace> consumer) {
        portal.provideGameSpaces(consumer);
    }

    @Override
    public GamePortalBackend.ActionType getActionType() {
        return this.quickPortal.getBackend().getActionType();
    }

    public GuiElement createGuiElement() {
        var element = GuiElementBuilder.from(this.icon().copy())
            .setItemName(Text.empty().append(this.name()))
            .hideDefaultTooltip();

        for (var line : this.description()) {
            var text = line.copy();

            if (line.getStyle().getColor() == null) {
                text.setStyle(line.getStyle().withFormatting(Formatting.GRAY));
            }

            element.addLoreLine(text);
        }

        var playerCount = this.getPlayerCount();
        var spectatorCount = this.getSpectatorCount();
        boolean allowSpace = true;

        if (playerCount > -1) {
            if (allowSpace) {
                element.addLoreLine(ScreenTexts.EMPTY);
                allowSpace = false;
            }
            element.addLoreLine(Text.empty()
                .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                .append(Text.translatable("text.plasmid.ui.game_join.players",
                    Text.literal(playerCount + "").formatted(Formatting.YELLOW)).formatted(Formatting.GOLD))
            );
        }

        if (spectatorCount > -1) {
            if (allowSpace) {
                element.addLoreLine(ScreenTexts.EMPTY);
                allowSpace = false;
            }

            element.addLoreLine(Text.empty()
                .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                .append(Text.translatable("text.plasmid.ui.game_join.spectators",
                    Text.literal(playerCount + "").formatted(Formatting.YELLOW)).formatted(Formatting.GOLD))
            );
        }

        var actionType = this.getActionType();

        if (actionType != GamePortalBackend.ActionType.NONE) {
            element.addLoreLine(Text.empty().append(Text.literal(" [ ").formatted(Formatting.GRAY))
                .append(actionType.text())
                .append(Text.literal(" ]").formatted(Formatting.GRAY)).setStyle(Style.EMPTY.withColor(0x76ed6f)));
        }
        element.addLoreLine(Text.empty().append(Text.literal(" [ ").formatted(Formatting.GRAY))
            .append(this.message().copy())
            .append(Text.literal(" ]").formatted(Formatting.GRAY)).setStyle(Style.EMPTY.withColor(0x5e8ad6)));

        element.setCallback((index, clickType, slotActionType, gui) -> {
            if (clickType.isRight) this.secondaryClick(gui.getPlayer());
            else this.click(gui.getPlayer(), false);
        });

        return element.build();
    }
}
