package xyz.nucleoid.extras.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.extras.placeholder.GameTextContent;

import java.util.function.Function;

@Mixin(ComponentSerialization.class)
public class TextCodecMixin {
    @ModifyReturnValue(method = "createCodec", at = @At("RETURN"))
    private static Codec<Component> addCustomTextType(Codec<Component> original) {
        return original.xmap(Function.identity(), text -> {
            if (text.getContents() instanceof GameTextContent gameTextContent) {
                return gameTextContent.toVanilla(PolymerCommonUtils.getPlayer(PacketContext.get()), text);
            }
            return text;
        });
    }
}
