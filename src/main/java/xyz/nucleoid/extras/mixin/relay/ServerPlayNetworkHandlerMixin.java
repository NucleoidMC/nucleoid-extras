package xyz.nucleoid.extras.mixin.relay;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;

import java.util.UUID;

@Mixin(value = PlayerManager.class, priority = 2000)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Inject(method = "broadcastChatMessage", at = @At(value = "RETURN"))
    private void onBroadcastChatMessage(Text message, MessageType type, UUID senderUuid, CallbackInfo ci) {
        if (type != MessageType.CHAT) return;
        ServerPlayerEntity playerEntity = getPlayer(senderUuid);
        if (playerEntity != null) {
            String content = ((TranslatableTextInvoker)message).invokeGetArg(1).getString();
            PlayerSendChatEvent.EVENT.invoker().onPlayerSendChat(playerEntity, content);
        }
    }
}
