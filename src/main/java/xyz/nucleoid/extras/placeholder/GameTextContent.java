package xyz.nucleoid.extras.placeholder;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

public record GameTextContent(GameSpace gameSpace) implements TextContent {
    public Text toVanilla(@Nullable ServerPlayerEntity player, Text text) {
        if (player == null) {
            var out = Text.empty();
            out.getSiblings().addAll(text.getSiblings());
            return out;
        }

        var playerSpace = GameSpaceManager.get().byWorld(player.getWorld());

        if (playerSpace == gameSpace) {
            var out = Text.empty();
            out.getSiblings().addAll(text.getSiblings());
            return out;
        }

        var out = Text.empty().append(
            Text.literal("◆").setStyle(
                Style.EMPTY
                    .withColor(
                        TextColor.fromRgb(gameSpace == null ? 0x800080 : (int) (gameSpace.getMetadata().id().getLeastSignificantBits() & 0xFFFFFF)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        gameSpace == null ? Text.literal("Lobby") : gameSpace.getMetadata().sourceConfig().name())))
        ).append(ScreenTexts.SPACE);

        out.getSiblings().addAll(text.getSiblings());

        return out;
    }
}
