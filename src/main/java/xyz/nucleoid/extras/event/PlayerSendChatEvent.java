package xyz.nucleoid.extras.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerSendChatEvent {
    Event<PlayerSendChatEvent> EVENT = EventFactory.createArrayBacked(PlayerSendChatEvent.class, handlers -> (player, message) -> {
        for (PlayerSendChatEvent event : handlers) {
            ActionResult result = event.onPlayerSendChat(player, message);
            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });

    ActionResult onPlayerSendChat(ServerPlayerEntity player, String content);
}
