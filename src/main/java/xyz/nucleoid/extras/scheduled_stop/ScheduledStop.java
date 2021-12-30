package xyz.nucleoid.extras.scheduled_stop;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import xyz.nucleoid.plasmid.event.GameEvents;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.config.GameConfig;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

import static net.minecraft.server.command.CommandManager.literal;

public final class ScheduledStop {
    private static final int FORCE_STOP_MINUTES = 5;
    private static final int FORCE_STOP_TIME = 20 * 60 * FORCE_STOP_MINUTES;

    private static final int GRACE_TIME = 20 * 5;

    private static boolean stopScheduled;
    private static int graceTime = Integer.MAX_VALUE;
    private static int stopTime = Integer.MAX_VALUE;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
            registerCommands(dispatcher)
        );

        ServerTickEvents.END_SERVER_TICK.register(ScheduledStop::tick);

        GameEvents.OPENED.register(ScheduledStop::openGame);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        // @formatter:off
        dispatcher.register(
                literal("stop").then(literal("schedule")
                    .requires(source -> source.hasPermissionLevel(4))
                    .executes(ScheduledStop::scheduleRestart)
                )
        );
        // @formatter:on
    }

    private static int scheduleRestart(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        if (!stopScheduled) {
            var server = source.getServer();
            int time = server.getTicks();

            stopScheduled = true;
            graceTime = time + GRACE_TIME;
            stopTime = time + FORCE_STOP_TIME;

            server.getPlayerManager().broadcast(
                    new TranslatableText("nucleoid.stop.scheduled", FORCE_STOP_MINUTES)
                            .formatted(Formatting.BOLD, Formatting.RED),
                    MessageType.SYSTEM,
                    Util.NIL_UUID
            );
        } else {
            source.sendError(new TranslatableText("nucleoid.stop.scheduled.already"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static void tick(MinecraftServer server) {
        if (stopScheduled && isReadyToStop(server)) {
            stopScheduled = false;
            stopTime = Integer.MAX_VALUE;
            graceTime = Integer.MAX_VALUE;

            for (var gameSpace : GameSpaceManager.get().getOpenGameSpaces()) {
                gameSpace.close(GameCloseReason.CANCELED);
            }

            server.stop(false);
        }
    }

    private static boolean isReadyToStop(MinecraftServer server) {
        int time = server.getTicks();
        if (time < graceTime) return false;
        if (time > stopTime) return true;

        var games = GameSpaceManager.get().getOpenGameSpaces();
        for (var game : games) {
            if (!game.getPlayers().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private static void openGame(GameConfig<?> config, GameSpace gameSpace) {
        if (stopScheduled) {
            throw new GameOpenException(new TranslatableText("nucleoid.stop.game.open"));
        }
    }
}
