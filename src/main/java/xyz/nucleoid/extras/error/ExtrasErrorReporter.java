package xyz.nucleoid.extras.error;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.crash.CrashReport;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameLifecycle;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.config.GameConfig;

import java.util.List;

public final class ExtrasErrorReporter {
    public static void register() {
        GameEvents.OPENED.register((game, gameSpace) -> {
            var config = NucleoidExtrasConfig.get();
            var errorReporting = config.errorReporting();

            var source = sourceName(gameSpace.getMetadata().sourceConfig());
            var errorHandler = errorReporting.openErrorHandler(source);
            if (errorHandler == null) {
                return;
            }

            gameSpace.getLifecycle().addListeners(new GameLifecycle.Listeners() {
                @Override
                public void onError(GameSpace gameSpace, Throwable throwable, String context) {
                    errorHandler.report(throwable, context);
                }

                @Override
                public void onClosed(GameSpace gameSpace, List<ServerPlayerEntity> players, GameCloseReason reason) {
                    errorHandler.close();
                }
            });
        });
    }

    private static String sourceName(GameConfig<?> game) {
        var name = game.name().getString();
        return name + " (" + game.source() + ")";
    }

    public static void onServerCrash(CrashReport report) {
        var config = NucleoidExtrasConfig.get();
        var errorReporting = config.errorReporting();

        var webhook = errorReporting.openDiscordWebhook();
        if (webhook != null) {
            var message = new DiscordWebhook.Message("The server has crashed!");
            message.addFile("report.txt", report.asString());

            webhook.post(message);
        }
    }
}
