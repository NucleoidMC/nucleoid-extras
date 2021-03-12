package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.ServerCommandSourceExt;

import java.util.Map;
import java.util.UUID;

public final class NucleoidCommandAliases {
    private static final CommandOutput NO_FEEDBACK_OUTPUT = new CommandOutput() {
        @Override
        public void sendSystemMessage(Text message, UUID senderUuid) {
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
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            NucleoidExtrasConfig config = NucleoidExtrasConfig.get();
            CommandAliasConfig aliases = config.getAliases();
            if (aliases == null) {
                return;
            }

            for (Map.Entry<String, CommandAliasConfig.Entry> entry : aliases.getMap().entrySet()) {
                LiteralArgumentBuilder<ServerCommandSource>[] literals = buildLiterals(entry);

                CommandAliasConfig.Entry value = entry.getValue();
                String[] commands = value.commands;
                literals[literals.length - 1].executes(context -> {
                    ServerCommandSource source = context.getSource().withMaxLevel(4);
                    if (!value.feedback) {
                        source = ((ServerCommandSourceExt) source).withOutput(NO_FEEDBACK_OUTPUT);
                    }

                    int result = Command.SINGLE_SUCCESS;
                    for (String command : commands) {
                        result = dispatcher.execute(command, source);
                    }
                    return result;
                });

                LiteralArgumentBuilder<ServerCommandSource> root = linkLiterals(literals);
                dispatcher.register(root);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static LiteralArgumentBuilder<ServerCommandSource>[] buildLiterals(Map.Entry<String, CommandAliasConfig.Entry> entry) {
        String[] names = entry.getKey().split(" ");

        LiteralArgumentBuilder<ServerCommandSource>[] literals = new LiteralArgumentBuilder[names.length];
        for (int i = 0; i < names.length; i++) {
            literals[i] = CommandManager.literal(names[i]);
        }

        return literals;
    }

    private static LiteralArgumentBuilder<ServerCommandSource> linkLiterals(LiteralArgumentBuilder<ServerCommandSource>[] literals) {
        LiteralArgumentBuilder<ServerCommandSource> chain = literals[0];
        for (int i = 1; i < literals.length; i++) {
            LiteralArgumentBuilder<ServerCommandSource> next = literals[i];
            chain.then(next);
            chain = next;
        }
        return literals[0];
    }
}
