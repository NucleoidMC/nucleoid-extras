package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import static net.minecraft.commands.Commands.literal;

public class SpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var config = NucleoidExtrasConfig.get();

        if (config.lobbySpawn() != null) {
            dispatcher.register(literal("spawn").executes(SpawnCommand::execute));

            // https://github.com/Mojang/brigadier/issues/46
            dispatcher.register(literal("lobby").executes(SpawnCommand::execute));
            dispatcher.register(literal("hub").executes(SpawnCommand::execute));
        }
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();

        var player = source.getPlayerOrException();
        var server = source.getServer();

        var gameSpace = GameSpaceManager.get().byPlayer(player);

        if (gameSpace != null) {
            gameSpace.getPlayers().kick(player);
        }

        var config = NucleoidExtrasConfig.get().lobbySpawn();

        config.teleport(player, server.overworld());
        config.changeGameMode(player, server.getDefaultGameType());

        NEItems.giveLobbyItems(player);

        return Command.SINGLE_SUCCESS;
    }
}
