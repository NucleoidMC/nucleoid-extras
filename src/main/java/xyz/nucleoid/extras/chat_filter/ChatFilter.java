package xyz.nucleoid.extras.chat_filter;

import net.minecraft.util.ActionResult;

import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerChatContentEvent;

public final class ChatFilter {
    public static void register() {
        var config = NucleoidExtrasConfig.get().chatFilter();
        if (config == null) {
            return;
        }

        Stimuli.global().listen(PlayerChatContentEvent.EVENT, (sender, message) -> {
            if (config.test(message.getRaw())) {
                config.sendFeedbackTo(sender);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
