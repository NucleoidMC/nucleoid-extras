package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.dialog.NEDialogs;

import static net.minecraft.commands.Commands.literal;

public class RulesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var config = NucleoidExtrasConfig.get();

        if (config.rules() != null) {
            dispatcher.register(literal("rules").executes(RulesCommand::execute));
        }
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.getPlayerOrException();

        var dialog = player.registryAccess().get(NEDialogs.RULES);

        if (dialog.isPresent()) {
            player.openDialog(dialog.get());
            return Command.SINGLE_SUCCESS;
        }

        return 0;
    }
}
