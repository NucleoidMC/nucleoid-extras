package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nucleoid.extras.NucleoidExtrasConfig;

import java.util.Map;
import java.util.UUID;

public final class CommandAliases {
    private static final CommandOutput NO_FEEDBACK_OUTPUT = new CommandOutput() {
        @Override
        public void sendMessage(Text message) {
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return false;
        }

        @Override
        public boolean shouldTrackOutput() {
            return false;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps() {
            return false;
        }
    };

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var config = NucleoidExtrasConfig.get();
            var aliases = config.aliases();
            if (aliases == null) {
                return;
            }

            for (var entry : aliases.map().entrySet()) {
                var literals = buildLiterals(entry);

                var value = entry.getValue();
                var commands = value.commands;
                literals[literals.length - 1].executes(context -> {
                    var source = context.getSource().withMaxLevel(4);
                    if (!value.feedback) {
                        source = source.withOutput(NO_FEEDBACK_OUTPUT);
                    }

                    int result = Command.SINGLE_SUCCESS;
                    for (var command : commands) {
                        result = dispatcher.execute(command, source);
                    }
                    return result;
                });

                var root = linkLiterals(literals);
                dispatcher.register(root);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static LiteralArgumentBuilder<ServerCommandSource>[] buildLiterals(Map.Entry<String, CommandAliasConfig.Entry> entry) {
        var names = entry.getKey().split(" ");

        LiteralArgumentBuilder<ServerCommandSource>[] literals = new LiteralArgumentBuilder[names.length];
        for (int i = 0; i < names.length; i++) {
            literals[i] = CommandManager.literal(names[i]);
        }

        return literals;
    }

    private static LiteralArgumentBuilder<ServerCommandSource> linkLiterals(LiteralArgumentBuilder<ServerCommandSource>[] literals) {
        var chain = literals[0];
        for (int i = 1; i < literals.length; i++) {
            var next = literals[i];
            chain.then(next);
            chain = next;
        }
        return literals[0];
    }
}
