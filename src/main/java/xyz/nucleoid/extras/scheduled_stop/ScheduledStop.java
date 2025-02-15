package xyz.nucleoid.extras.scheduled_stop;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.extras.event.NucleoidExtrasEvents;
import xyz.nucleoid.plasmid.api.event.GameEvents;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameOpenException;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;
import xyz.nucleoid.plasmid.api.game.config.GameConfig;

import java.util.ArrayList;

import static net.minecraft.server.command.CommandManager.literal;

public final class ScheduledStop {
    private static final int FORCE_STOP_MINUTES = 5;
    private static final int FORCE_STOP_TIME = 20 * 60 * FORCE_STOP_MINUTES;

    private static final int GRACE_TIME = 20 * 5;

    private static boolean stopScheduled;
    private static int graceTime = Integer.MAX_VALUE;
    private static int stopTime = Integer.MAX_VALUE;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            registerCommands(dispatcher)
        );

        NucleoidExtrasEvents.END_SERVER_TICK.register(ScheduledStop::tick);

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
                    Text.translatable("nucleoid.stop.scheduled", FORCE_STOP_MINUTES)
                            .formatted(Formatting.BOLD, Formatting.RED),
                    false
            );
        } else {
            source.sendError(Text.translatable("nucleoid.stop.scheduled.already"));
        }

        return Command.SINGLE_SUCCESS;
    }

    private static void tick(MinecraftServer server) {
        if (stopScheduled && isReadyToStop(server)) {
            stopScheduled = false;
            stopTime = Integer.MAX_VALUE;
            graceTime = Integer.MAX_VALUE;

            var games = new ArrayList<>(GameSpaceManager.get().getOpenGameSpaces());
            for (var gameSpace : games) {
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

    private static void openGame(RegistryEntry<GameConfig<?>> game, GameSpace gameSpace) {
        if (stopScheduled) {
            throw new GameOpenException(Text.translatable("nucleoid.stop.game.open"));
        }
    }
}
