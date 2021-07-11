package xyz.nucleoid.extras.chat_filter;

import net.minecraft.util.ActionResult;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

public final class ChatFilter {
    public static void register() {
        var config = NucleoidExtrasConfig.get().chatFilter();
        if (config == null) {
            return;
        }

        Stimuli.global().listen(PlayerChatEvent.EVENT, (sender, message) -> {
            var content = NucleoidExtras.getChatMessageContent(message);
            if (config.test(content)) {
                config.sendFeedbackTo(sender);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
