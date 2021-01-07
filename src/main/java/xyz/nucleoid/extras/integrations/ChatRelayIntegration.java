package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class ChatRelayIntegration {
    private final ConcurrentLinkedQueue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();

    private final IntegrationSender chatSender;

    private ChatRelayIntegration(IntegrationSender chatSender) {
        this.chatSender = chatSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.shouldSendChat()) {
            IntegrationSender chatSender = integrations.openSender("chat");

            ChatRelayIntegration integration = new ChatRelayIntegration(chatSender);

            integrations.bindReceiver("chat", body -> {
                String sender = body.get("sender").getAsString();
                String content = body.get("content").getAsString();

                TextColor nameColor = null;
                if (body.has("name_color")) {
                    nameColor = TextColor.fromRgb(body.get("name_color").getAsInt());
                }

                integration.messageQueue.add(new ChatMessage(sender, content.split("\n"), nameColor));
            });

            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
            PlayerSendChatEvent.EVENT.register(integration::onSendChatMessage);
        }
    }

    private void tick(MinecraftServer server) {
        ChatMessage message;
        while ((message = this.messageQueue.poll()) != null) {
            this.broadcastMessage(server, message);
        }
    }

    private void onSendChatMessage(ServerPlayerEntity player, String content) {
        JsonObject body = new JsonObject();

        GameProfile profile = player.getGameProfile();

        JsonObject senderRoot = new JsonObject();
        senderRoot.addProperty("id", profile.getId().toString());
        senderRoot.addProperty("name", profile.getName());

        body.add("sender", senderRoot);
        body.addProperty("content", content);

        this.chatSender.send(body);
    }

    private void broadcastMessage(MinecraftServer server, ChatMessage message) {
        for (String line : message.lines) {
            MutableText sender = new LiteralText(message.sender);
            if (message.nameColor != null) {
                sender = sender.setStyle(sender.getStyle().withColor(message.nameColor));
            }

            Text text = new LiteralText("<@").append(sender).append("> ").formatted(Formatting.GRAY)
                    .append(new LiteralText(line).formatted(Formatting.RESET));

            server.getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, Util.NIL_UUID);
        }
    }

    static class ChatMessage {
        final String sender;
        final String[] lines;
        final TextColor nameColor;

        ChatMessage(String sender, String[] lines, @Nullable TextColor nameColor) {
            this.sender = sender;
            this.lines = lines;
            this.nameColor = nameColor;
        }
    }
}
