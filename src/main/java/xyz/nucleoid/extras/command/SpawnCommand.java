package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import static net.minecraft.server.command.CommandManager.literal;

public class SpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var config = NucleoidExtrasConfig.get();

        if (config.lobbySpawn() != null) {
            dispatcher.register(literal("spawn").executes(SpawnCommand::execute));

            // https://github.com/Mojang/brigadier/issues/46
            dispatcher.register(literal("lobby").executes(SpawnCommand::execute));
            dispatcher.register(literal("hub").executes(SpawnCommand::execute));
        }
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();

        var player = source.getPlayerOrThrow();
        var server = source.getServer();

        GameSpaceManager.get().byPlayer(player).getPlayers().kick(player);

        var config = NucleoidExtrasConfig.get().lobbySpawn();

        config.teleport(player, server.getOverworld());
        config.changeGameMode(player, server.getDefaultGameMode());

        NEItems.giveLobbyItems(player);

        return Command.SINGLE_SUCCESS;
    }
}
