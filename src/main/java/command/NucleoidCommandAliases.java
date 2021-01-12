package command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import xyz.nucleoid.extras.NucleoidExtrasConfig;

import java.util.Map;

public final class NucleoidCommandAliases {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            NucleoidExtrasConfig config = NucleoidExtrasConfig.get();
            CommandAliasConfig aliases = config.getAliases();
            if (aliases == null) {
                return;
            }

            for (Map.Entry<String, String[]> entry : aliases.getMap().entrySet()) {
                LiteralArgumentBuilder<ServerCommandSource>[] literals = buildLiterals(entry);

                String[] commands = entry.getValue();
                literals[literals.length - 1].executes(context -> {
                    ServerCommandSource source = context.getSource().withMaxLevel(4);
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
    private static LiteralArgumentBuilder<ServerCommandSource>[] buildLiterals(Map.Entry<String, String[]> entry) {
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
