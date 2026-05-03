package xyz.nucleoid.extras.game_portal.entry;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.impl.portal.GamePortal;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntry;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record QuickPortalEntry(
    GamePortal portal,
    GamePortal quickPortal,
    Component message,
    Component name,
    List<Component> description,
    ItemStack icon
) implements MenuEntry {
    @Override
    public void click(ServerPlayer player, boolean alt) {
        this.quickPortal.requestJoin(player, alt);
    }

    public void secondaryClick(ServerPlayer player) {
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
            .setItemName(Component.empty().append(this.name()))
            .hideDefaultTooltip();

        for (var line : this.description()) {
            var text = line.copy();

            if (line.getStyle().getColor() == null) {
                text.setStyle(line.getStyle().applyFormat(ChatFormatting.GRAY));
            }

            element.addLoreLine(text);
        }

        var playerCount = this.getPlayerCount();
        var spectatorCount = this.getSpectatorCount();
        boolean allowSpace = true;

        if (playerCount > -1) {
            if (allowSpace) {
                element.addLoreLine(CommonComponents.EMPTY);
                allowSpace = false;
            }
            element.addLoreLine(Component.empty()
                .append(Component.literal("» ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable("text.plasmid.ui.game_join.players",
                    Component.literal(playerCount + "").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD))
            );
        }

        if (spectatorCount > -1) {
            if (allowSpace) {
                element.addLoreLine(CommonComponents.EMPTY);
                allowSpace = false;
            }

            element.addLoreLine(Component.empty()
                .append(Component.literal("» ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable("text.plasmid.ui.game_join.spectators",
                    Component.literal(playerCount + "").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD))
            );
        }

        var actionType = this.getActionType();

        if (actionType != GamePortalBackend.ActionType.NONE) {
            element.addLoreLine(Component.empty().append(Component.literal(" [ ").withStyle(ChatFormatting.GRAY))
                .append(actionType.text())
                .append(Component.literal(" ]").withStyle(ChatFormatting.GRAY)).setStyle(Style.EMPTY.withColor(0x76ed6f)));
        }
        element.addLoreLine(Component.empty().append(Component.literal(" [ ").withStyle(ChatFormatting.GRAY))
            .append(this.message().copy())
            .append(Component.literal(" ]").withStyle(ChatFormatting.GRAY)).setStyle(Style.EMPTY.withColor(0x5e8ad6)));

        element.setCallback((index, clickType, slotActionType, gui) -> {
            if (clickType.isRight) this.secondaryClick(gui.getPlayer());
            else this.click(gui.getPlayer(), false);
        });

        return element.build();
    }
}
