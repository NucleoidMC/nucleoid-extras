package xyz.nucleoid.extras.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

import static net.minecraft.server.command.CommandManager.literal;

public class GamemodeFixCommand {
    private static final SimpleCommandExceptionType IN_GAME = new SimpleCommandExceptionType(new TranslatableText("nucleoid.error.in_game"));
    private static final SimpleCommandExceptionType CORRECT_GAMEMODE = new SimpleCommandExceptionType(new TranslatableText("nucleoid.error.correct_game_mode"));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("helpmeiaminthewronggamemode").executes(GamemodeFixCommand::correctGameMode))
        );
    }

    private static int correctGameMode(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (ManagedGameSpace.forWorld(player.world) != null) {
            throw IN_GAME.create();
        }

        if (player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
            player.setGameMode(GameMode.ADVENTURE);
        } else {
            throw CORRECT_GAMEMODE.create();
        }

        ctx.getSource().sendFeedback(new TranslatableText("nucleoid.gamemode.fixed"), false);

        return Command.SINGLE_SUCCESS;
    }
}
