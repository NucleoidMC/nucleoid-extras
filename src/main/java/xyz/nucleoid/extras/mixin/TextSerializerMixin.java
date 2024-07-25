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
            return gameTextContent.toVanilla(PacketContext.get().getTarget(), text);
        }

        return text;
    }
}
