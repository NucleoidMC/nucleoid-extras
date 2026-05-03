package xyz.nucleoid.extras.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ExtraCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(ExtraCommands::register);
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        RulesCommand.register(dispatcher);
        SpawnCommand.register(dispatcher);
        StatsCommand.register(dispatcher);
    }
}
