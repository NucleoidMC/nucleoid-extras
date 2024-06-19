package xyz.nucleoid.extras.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.extras.placeholder.GameTextContent;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Function;

@Mixin(TextCodecs.class)
public class TextCodecMixin {
    @ModifyReturnValue(method = "createCodec", at = @At("RETURN"))
    private static Codec<Text> addCustomTextType(Codec<Text> original) {
        return original.xmap(Function.identity(), text -> {
            if (text.getContent() instanceof GameTextContent gameTextContent) {
                return gameTextContent.toVanilla(PacketContext.get().getPlayer(), text);
            }
            return text;
        });
    }
}
