package xyz.nucleoid.extras.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.extras.placeholder.GameTextContent;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

@Mixin(ComponentSerialization.class)
public class TextCodecMixin {
    @ModifyReturnValue(method = "createCodec", at = @At("RETURN"))
    private static Codec<Component> addCustomTextType(Codec<Component> original) {
        return original.xmap(Function.identity(), text -> {
            if (text.getContents() instanceof GameTextContent gameTextContent) {
                return gameTextContent.toVanilla(PacketContext.get().getPlayer(), text);
            }
            return text;
        });
    }
}
