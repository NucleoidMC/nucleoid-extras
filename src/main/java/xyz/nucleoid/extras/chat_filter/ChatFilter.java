package xyz.nucleoid.extras.chat_filter;

import net.minecraft.util.ActionResult;

import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

public final class ChatFilter {
    public static void register() {
        var config = NucleoidExtrasConfig.get().chatFilter();
        if (config == null) {
            return;
        }

        Stimuli.global().listen(PlayerChatEvent.EVENT, (player, message, parameters) -> {
            if (config.test(message.getContent().getString())) {
                config.sendFeedbackTo(player);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
