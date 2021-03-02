package xyz.nucleoid.extras.mixin.relay;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Mixin(value = PlayerManager.class, priority = 2000)
public abstract class PlayerManagerMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Inject(method = "broadcastChatMessage", at = @At(value = "RETURN"))
    private void onBroadcastChatMessage(Text message, MessageType type, UUID senderUuid, CallbackInfo ci) {
        if (type != MessageType.CHAT || senderUuid == Util.NIL_UUID) return;

        ServerPlayerEntity player = getPlayer(senderUuid);
        if (player != null) {
            String content = getContent(message);
            if (content != null) {
                PlayerSendChatEvent.EVENT.invoker().onPlayerSendChat(player, content);
            }
        }
    }

    @Nullable
    private static String getContent(Text text) {
        if (text instanceof TranslatableText) {
            Object[] args = ((TranslatableText) text).getArgs();
            if (args.length == 2) {
                Object content = args[1];
                if (content instanceof String) {
                    return ((String) content);
                } else if (content instanceof StringVisitable) {
                    return ((StringVisitable) content).getString();
                }
            }
        } else {
            return text.getString();
        }
        return null;
    }
}
