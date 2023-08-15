package xyz.nucleoid.extras.game_portal.entry;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.portal.GamePortal;
import xyz.nucleoid.plasmid.game.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntry;

import java.util.List;
import java.util.function.Consumer;

public record QuickPortalEntry(
    GamePortal portal,
    GamePortal quickPortal,
    Text name,
    List<Text> description,
    ItemStack icon
) implements MenuEntry {
    @Override
    public void click(ServerPlayerEntity player) {
        this.quickPortal.requestJoin(player);
    }

    public void secondaryClick(ServerPlayerEntity player) {
        this.portal.requestJoin(player);
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
        var element = GuiElementBuilder.from(this.icon().copy()).hideFlags()
            .setName(Text.empty().append(this.name()));

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
            .append(Text.translatable("text.nucleoid_extras.ui.action.more"))
            .append(Text.literal(" ]").formatted(Formatting.GRAY)).setStyle(Style.EMPTY.withColor(0x5e8ad6)));

        element.setCallback((index, clickType, slotActionType, gui) -> {
            if (clickType.isRight) this.secondaryClick(gui.getPlayer());
            else this.click(gui.getPlayer());
        });

        return element.build();
    }
}
