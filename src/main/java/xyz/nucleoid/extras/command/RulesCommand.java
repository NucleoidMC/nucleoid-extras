package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.dialog.NEDialogs;

import static net.minecraft.server.command.CommandManager.literal;

public class RulesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var config = NucleoidExtrasConfig.get();

        if (config.rules() != null) {
            dispatcher.register(literal("rules").executes(RulesCommand::execute));
        }
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrThrow();

        var dialog = player.getRegistryManager().getOptionalEntry(NEDialogs.RULES);

        if (dialog.isPresent()) {
            player.openDialog(dialog.get());
            return Command.SINGLE_SUCCESS;
        }

        return 0;
    }
}
