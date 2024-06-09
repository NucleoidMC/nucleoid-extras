package xyz.nucleoid.extras.error;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameLifecycle;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.config.GameConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtrasErrorReporter {
    public static final CustomErrorType TOO_FAST = new CustomErrorType(Duration.ofMinutes(1));

    private static final Map<CustomErrorType, Instant> LAST_REPORT = new ConcurrentHashMap<>();

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
        var webhook = openWebhook();
        if (webhook != null) {
            var message = new DiscordWebhook.Message("The server has crashed!");
            message.addFile("report.txt", report.asString());
            webhook.post(message);
        }
    }

    public static void reportCustom(CustomErrorType type, Throwable throwable) {
        var now = Instant.now();
        var lastReport = LAST_REPORT.get(type);
        var timeSinceLastReport = Duration.between(lastReport, now);
        if (timeSinceLastReport.compareTo(type.reportInterval) < 0) {
            return;
        }

        if (!LAST_REPORT.replace(type, lastReport, now)) {
            return;
        }

        var webhook = openWebhook();
        if (webhook != null) {
            var message = new DiscordWebhook.Message("An error has occurred!");
            try (var writer = new StringWriter()) {
                throwable.printStackTrace(new PrintWriter(writer));
                message.addFile("trace.txt", writer.toString());
                webhook.post(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    private static DiscordWebhook openWebhook() {
        var errorReporting = NucleoidExtrasConfig.get().errorReporting();
        return errorReporting.openDiscordWebhook();
    }

    public static class CustomErrorType {
        private final Duration reportInterval;

        private CustomErrorType(Duration reportInterval) {
            this.reportInterval = reportInterval;
        }
    }
}
