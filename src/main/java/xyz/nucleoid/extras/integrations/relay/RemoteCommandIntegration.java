package xyz.nucleoid.extras.integrations.relay;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class RemoteCommandIntegration {
    private static final String DISCORD_EVERYONE_ROLE = "discord_everyone";

    private final ConcurrentLinkedQueue<RemoteCommand> commandQueue = new ConcurrentLinkedQueue<>();

    private final IntegrationSender systemSender;

    private RemoteCommandIntegration(IntegrationSender systemSender) {
        this.systemSender = systemSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.acceptRemoteCommands()) {
            var systemSender = integrations.openSender("system");

            var integration = new RemoteCommandIntegration(systemSender);

            integrations.bindReceiver("command", body -> {
                var command = body.get("command").getAsString();
                var sender = body.get("sender").getAsString();
                var discordRoles = body.get("roles").getAsJsonArray();
                var roles = new ArrayList<String>();
                roles.add(DISCORD_EVERYONE_ROLE);
                var permissionLevel = 0;
                for (var roleId : discordRoles) {
                    var role = config.discordRoleMap().get(roleId.getAsString());
                    if (role != null) {
                        roles.add(role);
                    }
                    var level = config.discordPermissionMap().get(roleId.getAsString());
                    if (level != null && level > permissionLevel) {
                        permissionLevel = level;
                    }
                }

                integration.commandQueue.add(new RemoteCommand(command, sender, permissionLevel, roles));
            });

            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        RemoteCommand command;
        while ((command = this.commandQueue.poll()) != null) {
            var commandSource = command.createCommandSource(server, this::sendCommandResult);
            server.getCommandManager().execute(commandSource, command.command);
        }
    }

    private void sendCommandResult(Text text) {
        var body = new JsonObject();
        body.addProperty("content", text.getString());
        this.systemSender.send(body);
    }

    record RemoteCommand(String command, String sender, int permissionLevel, List<String> roles) {
        ServerCommandSource createCommandSource(MinecraftServer server, Consumer<Text> result) {
            var output = new CommandOutput() {
                @Override
                public void sendSystemMessage(Text message, UUID senderUuid) {
                    result.accept(message);
                }

                @Override
                public boolean shouldReceiveFeedback() {
                    return true;
                }

                @Override
                public boolean shouldTrackOutput() {
                    return true;
                }

                @Override
                public boolean shouldBroadcastConsoleToOps() {
                    return true;
                }
            };

            var name = "@" + this.sender;
            return CommandSourceBuilder.INSTANCE.buildCommandSource(output, server, name, this.permissionLevel, this.roles);
        }
    }
}
