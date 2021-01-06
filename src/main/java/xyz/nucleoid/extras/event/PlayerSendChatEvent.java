package xyz.nucleoid.extras.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerSendChatEvent {
    Event<PlayerSendChatEvent> EVENT = EventFactory.createArrayBacked(PlayerSendChatEvent.class, handlers -> (player, message) -> {
        for (PlayerSendChatEvent event : handlers) {
            event.onPlayerSendChat(player, message);
        }
    });

    void onPlayerSendChat(ServerPlayerEntity player, String content);
}
