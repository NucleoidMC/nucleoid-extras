package xyz.nucleoid.extras.placeholder;

import com.mojang.serialization.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.text.*;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;

import java.util.stream.Stream;

public record GameTextContent(GameSpace gameSpace) implements ComponentContents {
    public Component toVanilla(@Nullable ServerPlayer player, Component text) {
        if (player == null) {
            var out = Component.empty();
            out.getSiblings().addAll(text.getSiblings());
            return out;
        }

        var playerSpace = GameSpaceManager.get().byWorld(player.level());

        if (playerSpace == gameSpace) {
            var out = Component.empty();
            out.getSiblings().addAll(text.getSiblings());
            return out;
        }

        var out = Component.empty().append(
            Component.literal("◆").setStyle(
                Style.EMPTY
                    .withColor(
                        TextColor.fromRgb(gameSpace == null ? 0x800080 : (int) (gameSpace.getMetadata().id().getLeastSignificantBits() & 0xFFFFFF)))
                    .withHoverEvent(new HoverEvent.ShowText(
                        gameSpace == null ? Component.literal("Lobby") :  GameConfig.name(gameSpace.getMetadata().sourceConfig()))))
        ).append(CommonComponents.SPACE);

        out.getSiblings().addAll(text.getSiblings());

        return out;
    }

    @Override
    public MapCodec<? extends ComponentContents> codec() {
        return null;
    }
}
