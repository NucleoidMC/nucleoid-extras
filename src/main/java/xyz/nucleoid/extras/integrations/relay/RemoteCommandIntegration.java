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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RemoteCommandIntegration {
    private static final String DISCORD_EVERYONE_ROLE = "discord_everyone";
    private static final int MAX_RESULT_LINES = 5;

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
                var silent = body.has("silent") && body.get("silent").getAsBoolean();
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

                integration.commandQueue.add(new RemoteCommand(command, sender, silent, permissionLevel, roles));
            });

            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        RemoteCommand command;
        while ((command = this.commandQueue.poll()) != null) {
            var results = new ArrayList<Text>();
            var commandSource = command.createCommandSource(server, results::add);
            server.getCommandManager().executeWithPrefix(commandSource, command.command);
            sendCommandResults(results);
        }
    }

    private void sendCommandResults(List<Text> results) {
        if (results.isEmpty()) {
            return;
        }
        var content = results.stream()
            .limit(MAX_RESULT_LINES)
            .map(Text::getString)
            .collect(Collectors.joining("\n"));
        if (results.size() > MAX_RESULT_LINES) {
            content += "\n...and " + (results.size() - MAX_RESULT_LINES) + " more lines";
        }
        var body = new JsonObject();
        body.addProperty("content", content);
        this.systemSender.send(body);
    }

    record RemoteCommand(String command, String sender, boolean silent, int permissionLevel, List<String> roles) {
        ServerCommandSource createCommandSource(MinecraftServer server, Consumer<Text> result) {
            var output = new CommandOutput() {
                @Override
                public void sendMessage(Text message) {
                    if (!silent) result.accept(message);
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
