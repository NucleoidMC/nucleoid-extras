package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class RemoteCommandIntegration {
    private final ConcurrentLinkedQueue<RemoteCommand> commandQueue = new ConcurrentLinkedQueue<>();

    private final IntegrationSender systemSender;

    private RemoteCommandIntegration(IntegrationSender systemSender) {
        this.systemSender = systemSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.shouldAcceptRemoteCommands()) {
            IntegrationSender systemSender = integrations.openSender("system");

            RemoteCommandIntegration integration = new RemoteCommandIntegration(systemSender);

            integrations.bindReceiver("command", body -> {
                String command = body.get("command").getAsString();
                String sender = body.get("sender").getAsString();
                integration.commandQueue.add(new RemoteCommand(command, sender));
            });

            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
        }
    }

    private void tick(MinecraftServer server) {
        RemoteCommand command;
        while ((command = this.commandQueue.poll()) != null) {
            ServerCommandSource commandSource = command.createCommandSource(server, this::sendCommandResult);
            server.getCommandManager().execute(commandSource, command.command);
        }
    }

    private void sendCommandResult(Text text) {
        JsonObject body = new JsonObject();
        body.addProperty("content", text.getString());
        this.systemSender.send(body);
    }

    static final class RemoteCommand {
        final String command;
        final String sender;

        RemoteCommand(String command, String sender) {
            this.command = command;
            this.sender = sender;
        }

        ServerCommandSource createCommandSource(MinecraftServer server, Consumer<Text> result) {
            CommandOutput output = new CommandOutput() {
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

            String name = "@" + this.sender;
            return new ServerCommandSource(output, Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), 4, name, new LiteralText(name), server, null);
        }
    }
}
