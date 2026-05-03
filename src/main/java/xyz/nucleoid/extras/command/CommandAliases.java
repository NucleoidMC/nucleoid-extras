package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xyz.nucleoid.extras.NucleoidExtrasConfig;

import java.util.Map;
import java.util.UUID;

public final class CommandAliases {
    private static final CommandSource NO_FEEDBACK_OUTPUT = new CommandSource() {
        @Override
        public void sendSystemMessage(Component message) {
        }

        @Override
        public boolean acceptsSuccess() {
            return false;
        }

        @Override
        public boolean acceptsFailure() {
            return false;
        }

        @Override
        public boolean shouldInformAdmins() {
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
                    var source = context.getSource().withMaximumPermission(4);
                    if (!value.feedback) {
                        source = source.withSource(NO_FEEDBACK_OUTPUT);
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
    private static LiteralArgumentBuilder<CommandSourceStack>[] buildLiterals(Map.Entry<String, CommandAliasConfig.Entry> entry) {
        var names = entry.getKey().split(" ");

        LiteralArgumentBuilder<CommandSourceStack>[] literals = new LiteralArgumentBuilder[names.length];
        for (int i = 0; i < names.length; i++) {
            literals[i] = Commands.literal(names[i]);
        }

        return literals;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> linkLiterals(LiteralArgumentBuilder<CommandSourceStack>[] literals) {
        var chain = literals[0];
        for (int i = 1; i < literals.length; i++) {
            var next = literals[i];
            chain.then(next);
            chain = next;
        }
        return literals[0];
    }
}
