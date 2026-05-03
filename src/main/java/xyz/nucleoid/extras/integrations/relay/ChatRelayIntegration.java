package xyz.nucleoid.extras.integrations.relay;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.text.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.event.NucleoidExtrasEvents;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;

import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ChatRelayIntegration {
    private final ConcurrentLinkedQueue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();

    private final IntegrationSender chatSender;

    private ChatRelayIntegration(IntegrationSender chatSender) {
        this.chatSender = chatSender;
    }

    public static void bind(NucleoidIntegrations integrations, IntegrationsConfig config) {
        if (config.sendChat()) {
            var chatSender = integrations.openSender("chat");

            var integration = new ChatRelayIntegration(chatSender);

            integrations.bindReceiver("chat", body -> {
                var message = parseMessage(body);
                integration.messageQueue.add(message);
            });

            NucleoidExtrasEvents.END_SERVER_TICK.register(integration::tick);

            ServerMessageEvents.CHAT_MESSAGE.register((message, sender, parameters) -> {
                integration.onSendChatMessage(sender, message.decoratedContent().getString());
            });
        }
    }

    @NotNull
    private static ChatMessage parseMessage(JsonObject body) {
        var sender = body.get("sender").getAsString();

        var senderUserId = sender;
        if (body.has("sender_user")) {
            senderUserId = parseUserId(body.getAsJsonObject("sender_user"));
        }

        var content = body.get("content").getAsString();

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

        ChatMessage replyingTo;
        if (body.has("replying_to")) {
            replyingTo = parseMessage(body.getAsJsonObject("replying_to"));
        } else {
            replyingTo = null;
        }

        return new ChatMessage(sender, senderUserId, content.split("\n"), nameColor, attachments, replyingTo);
    }

    private static String parseUserId(JsonObject user) {
        return user.get("name").getAsString();
    }

    @NotNull
    private static Attachment[] parseAttachments(JsonObject body) {
        var attachmentsArray = body.getAsJsonArray("attachments");
        var attachments = new Attachment[attachmentsArray.size()];

        int i = 0;
        for (var element : attachmentsArray) {
            var attachment = element.getAsJsonObject();
            var attachmentName = attachment.get("name").getAsString();
            var attachmentUrl = attachment.get("url").getAsString();
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

    private void onSendChatMessage(ServerPlayer player, String content) {
        var body = new JsonObject();

        var senderRoot = new JsonObject();
        senderRoot.addProperty("id", player.getStringUUID());
        senderRoot.addProperty("name", player.getGameProfile().name());

        body.add("sender", senderRoot);
        body.addProperty("content", content);

        this.chatSender.send(body);

    }

    private void broadcastMessage(MinecraftServer server, ChatMessage message) {
        var playerManager = server.getPlayerList();

        var sender = message.getSenderName();
        var prefix = Component.literal("<@").append(sender).append(">").withStyle(ChatFormatting.GRAY);
        var result = new MessageBuilder(prefix);

        if (message.replyingTo != null) {
            result.append(this.createReplyText(message.replyingTo));
        }

        for (var line : message.lines) {
            result.append(Component.literal(line));
        }

        if (message.attachments != null) {
            for (var attachment : message.attachments) {
                result.append(Component.literal("[Attachment: " + attachment.name + "]").withStyle(style ->
                    style.applyFormats(ChatFormatting.BLUE, ChatFormatting.UNDERLINE)
                            .withClickEvent(new ClickEvent.OpenUrl(URI.create(attachment.url)))
                            .withHoverEvent(new HoverEvent.ShowText(Component.literal("Open attachment")))
                ));
            }
        }

        playerManager.broadcastSystemMessage(result.build(), false);
    }

    private MutableComponent createReplyText(ChatMessage message) {
        var summary = message.getSummary();

        var replyText = Component.literal("(replying to @")
                .append(message.getSenderName());

        if (summary != null) {
            replyText = replyText.append(": ").append(Component.literal(summary).withStyle(ChatFormatting.ITALIC));
        }

        replyText = replyText.append(")");

        return replyText.withStyle(ChatFormatting.GRAY);
    }

    record ChatMessage(
            String sender, String senderUserId,
            String[] lines,
            @Nullable TextColor nameColor,
            @Nullable Attachment[] attachments,
            @Nullable ChatRelayIntegration.ChatMessage replyingTo
    ) {
        private static final int SUMMARY_LENGTH = 40;

        MutableComponent getSenderName() {
            MutableComponent sender = Component.literal(this.sender);
            if (this.nameColor != null) {
                var style = sender.getStyle()
                        .withColor(this.nameColor)
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(this.senderUserId)));
                sender = sender.setStyle(style);
            }
            return sender;
        }

        @Nullable
        String getSummary() {
            if (this.lines.length > 0) {
                var line = this.lines[0];
                if (line.length() <= SUMMARY_LENGTH) {
                    return line;
                } else {
                    return line.substring(0, SUMMARY_LENGTH - 2) + "..";
                }
            }
            return null;
        }
    }

    record Attachment(String name, String url) {
    }

    static class MessageBuilder {
        private static final MutableComponent NEW_LINE = Component.literal("\n | ").withStyle(ChatFormatting.GRAY);

        MutableComponent text;
        boolean first = true;

        MessageBuilder(Component prefix) {
            this.text = Component.empty().append(prefix).append(CommonComponents.SPACE);
        }

        void append(MutableComponent text) {
            if (this.first) {
                this.text = this.text.append(text);
                this.first = false;
            } else {
                this.text = this.text.append(NEW_LINE).append(text);
            }
        }

        MutableComponent build() {
            return this.text;
        }
    }
}
