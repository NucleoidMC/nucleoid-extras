package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
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
                ChatMessage message = parseMessage(body);
                integration.messageQueue.add(message);
            });

            ServerTickEvents.END_SERVER_TICK.register(integration::tick);
            PlayerSendChatEvent.EVENT.register(integration::onSendChatMessage);
        }
    }

    @NotNull
    private static ChatMessage parseMessage(JsonObject body) {
        String sender = body.get("sender").getAsString();
        String content = body.get("content").getAsString();

        TextColor nameColor = null;
        if (body.has("name_color")) {
            nameColor = TextColor.fromRgb(body.get("name_color").getAsInt());
        }

        Attachment[] attachments;
        if (body.has("attachments")) {
            attachments = parseAttachments(body);
        } else {
            attachments = null;
        }

        return new ChatMessage(sender, content.split("\n"), nameColor, attachments);
    }

    @NotNull
    private static Attachment[] parseAttachments(JsonObject body) {
        JsonArray attachmentsArray = body.getAsJsonArray("attachments");
        Attachment[] attachments = new Attachment[attachmentsArray.size()];

        int i = 0;
        for (JsonElement element : attachmentsArray) {
            JsonObject attachment = element.getAsJsonObject();
            String attachmentName = attachment.get("name").getAsString();
            String attachmentUrl = attachment.get("url").getAsString();
            attachments[i++] = new Attachment(attachmentName, attachmentUrl);
        }

        return attachments;
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

            MutableText text = new LiteralText("<@").append(sender).append("> ").formatted(Formatting.GRAY)
                    .append(new LiteralText(line).formatted(Formatting.WHITE));

            if (message.attachments != null) {
                for (Attachment attachment : message.attachments) {
                    text = text.append(new LiteralText("\n[Attachment: " + attachment.name + "]").styled(style -> {
                        return style.withColor(Formatting.BLUE).withUnderline(true)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.url))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open attachment")));
                    }));
                }
            }

            server.getPlayerManager().broadcastChatMessage(text, MessageType.CHAT, Util.NIL_UUID);
        }
    }

    static class ChatMessage {
        final String sender;
        final String[] lines;
        final TextColor nameColor;
        final Attachment[] attachments;

        ChatMessage(String sender, String[] lines, @Nullable TextColor nameColor, @Nullable Attachment[] attachments) {
            this.sender = sender;
            this.lines = lines;
            this.nameColor = nameColor;
            this.attachments = attachments;
        }
    }

    static class Attachment {
        final String name;
        final String url;

        Attachment(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}
