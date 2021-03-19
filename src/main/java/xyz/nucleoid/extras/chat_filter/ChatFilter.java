package xyz.nucleoid.extras.chat_filter;

import net.minecraft.util.ActionResult;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;

public final class ChatFilter {
    public static void register() {
        ChatFilterConfig config = NucleoidExtrasConfig.get().getChatFilter();
        if (config == null) {
            return;
        }

        PlayerSendChatEvent.EVENT.register((player, content) -> {
            if (config.test(content)) {
                config.sendFeedbackTo(player);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
