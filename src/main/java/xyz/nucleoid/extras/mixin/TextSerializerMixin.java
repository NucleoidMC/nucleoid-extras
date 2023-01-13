package xyz.nucleoid.extras.mixin;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nucleoid.extras.placeholder.GameTextContent;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

@Mixin(Text.Serializer.class)
public class TextSerializerMixin {
    @ModifyVariable(method = "serialize(Lnet/minecraft/text/Text;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), argsOnly = true)
    private Text extras$serializeText(Text text) {
        if (text.getContent() instanceof GameTextContent gameTextContent) {
            var player = PacketContext.get().getTarget();

            if (player == null) {
                var out = Text.empty();
                out.getSiblings().addAll(text.getSiblings());
                return out;
            }

            var playerSpace = GameSpaceManager.get().byWorld(player.world);

            if (playerSpace == gameTextContent.gameSpace()) {
                var out = Text.empty();
                out.getSiblings().addAll(text.getSiblings());
                return out;
            }

            var out = Text.empty().append(
                Texts.bracketed(Text.literal("◆").setStyle(Style.EMPTY.withColor(
                    TextColor.fromRgb(gameTextContent.gameSpace() == null ? 0x800080 : (int) (gameTextContent.gameSpace().getMetadata().id().getLeastSignificantBits() & 0xFFFFFF)))))
                    .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        gameTextContent.gameSpace() == null ? Text.literal("Lobby") : gameTextContent.gameSpace().getMetadata().sourceConfig().name())))
            ).append(" ");
            out.getSiblings().addAll(text.getSiblings());
            return out;
        }

        return text;
    }
}
