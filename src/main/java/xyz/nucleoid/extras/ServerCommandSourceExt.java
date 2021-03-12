package xyz.nucleoid.extras;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;

public interface ServerCommandSourceExt {
    ServerCommandSource withOutput(CommandOutput output);
}
