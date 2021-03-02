package xyz.nucleoid.extras.mixin.relay;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;

import java.util.UUID;

@Mixin(value = PlayerManager.class, priority = 2000)
public abstract class PlayerManagerMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Inject(method = "broadcastChatMessage", at = @At(value = "RETURN"))
    private void onBroadcastChatMessage(Text message, MessageType type, UUID senderUuid, CallbackInfo ci) {
        if (type != MessageType.CHAT) return;
        ServerPlayerEntity playerEntity = getPlayer(senderUuid);
        if (playerEntity != null) {
            if (message instanceof TranslatableText) {
                Object[] args = ((TranslatableText) message).getArgs();
                if (args.length == 2) {
                    String content = args[1].getString();
                    PlayerSendChatEvent.EVENT.invoker().onPlayerSendChat(playerEntity, content);
                }
            }
        }
    }
}
