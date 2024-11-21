package xyz.nucleoid.extras.error;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ReportType;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.plasmid.api.event.GameEvents;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameLifecycle;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameType;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;
import xyz.nucleoid.plasmid.api.game.config.GameConfigs;
import xyz.nucleoid.plasmid.impl.Plasmid;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class ExtrasErrorReporter {
    public static final CustomErrorType TOO_FAST = new CustomErrorType(Duration.ofMinutes(1));
    public static final CustomErrorType INVALID_GAMES = new CustomErrorType(Duration.ofMinutes(1));

    private static final Map<CustomErrorType, Instant> LAST_REPORT = new ConcurrentHashMap<>();

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var invalidType = GameType.get(Identifier.of(Plasmid.ID, "invalid"));

            var invalidGames = server.getRegistryManager()
                    .getOrThrow(GameConfigs.REGISTRY_KEY)
                    .streamEntries()
                    .filter(entry -> entry.value().type() == invalidType)
                    .map(ExtrasErrorReporter::sourceName)
                    .map(entry -> " - " + entry)
                    .sorted()
                    .toList();

            if (!invalidGames.isEmpty()) {
                var lines = String.join("\n", invalidGames);
                var trace = String.format("Loaded %d invalid game(s):\n\n%s", invalidGames.size(), lines);

                reportCustom(INVALID_GAMES, trace);
            }
        });

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

    private static String sourceName(RegistryEntry<GameConfig<?>> game) {
        var name = GameConfig.name(game).getString();
        return name + " (" + GameConfig.sourceName(game) + ")";
    }

    public static void onServerCrash(CrashReport report) {
        var webhook = openWebhook();
        if (webhook != null) {
            var message = new DiscordWebhook.Message("The server has crashed!");
            message.addFile("report.txt", report.asString(ReportType.MINECRAFT_CRASH_REPORT));
            webhook.post(message);
        }
    }

    public static void reportCustom(CustomErrorType type, Throwable throwable) {
        reportCustom(type, writer -> {
            throwable.printStackTrace(new PrintWriter(writer));
        });
    }

    public static void reportCustom(CustomErrorType type, String trace) {
        reportCustom(type, writer -> {
            writer.write(trace);
        });
    }

    private static void reportCustom(CustomErrorType type, Consumer<StringWriter> trace) {
        if (!updateLastReportTime(type, Instant.now())) {
            return;
        }
        var webhook = openWebhook();
        if (webhook != null) {
            var message = new DiscordWebhook.Message("An error has occurred!");
            try (var writer = new StringWriter()) {
                trace.accept(writer);
                message.addFile("trace.txt", writer.toString());
                webhook.post(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean updateLastReportTime(CustomErrorType type, Instant now) {
        var lastReport = LAST_REPORT.putIfAbsent(type, now);
        if (lastReport != null) {
            var timeSinceLastReport = Duration.between(lastReport, now);
            if (timeSinceLastReport.compareTo(type.reportInterval) < 0) {
                return false;
            }
            return LAST_REPORT.replace(type, lastReport, now);
        }
        return true;
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
