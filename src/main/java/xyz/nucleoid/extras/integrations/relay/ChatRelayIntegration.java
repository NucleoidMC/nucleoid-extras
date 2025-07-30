package xyz.nucleoid.extras.integrations.relay;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
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
                integration.onSendChatMessage(sender, message.getContent().getString());
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

    private void onSendChatMessage(ServerPlayerEntity player, String content) {
        var body = new JsonObject();

        var senderRoot = new JsonObject();
        senderRoot.addProperty("id", player.getUuidAsString());
        senderRoot.addProperty("name", player.getGameProfile().getName());

        body.add("sender", senderRoot);
        body.addProperty("content", content);

        this.chatSender.send(body);

    }

    private void broadcastMessage(MinecraftServer server, ChatMessage message) {
        var playerManager = server.getPlayerManager();

        var sender = message.getSenderName();
        var prefix = Text.literal("<@").append(sender).append(">").formatted(Formatting.GRAY);
        var result = new MessageBuilder(prefix);

        if (message.replyingTo != null) {
            result.append(this.createReplyText(message.replyingTo));
        }

        for (var line : message.lines) {
            result.append(Text.literal(line));
        }

        if (message.attachments != null) {
            for (var attachment : message.attachments) {
                result.append(Text.literal("[Attachment: " + attachment.name + "]").styled(style ->
                    style.withFormatting(Formatting.BLUE, Formatting.UNDERLINE)
                            .withClickEvent(new ClickEvent.OpenUrl(URI.create(attachment.url)))
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Open attachment")))
                ));
            }
        }

        playerManager.broadcast(result.build(), false);
    }

    private MutableText createReplyText(ChatMessage message) {
        var summary = message.getSummary();

        var replyText = Text.literal("(replying to @")
                .append(message.getSenderName());

        if (summary != null) {
            replyText = replyText.append(": ").append(Text.literal(summary).formatted(Formatting.ITALIC));
        }

        replyText = replyText.append(")");

        return replyText.formatted(Formatting.GRAY);
    }

    record ChatMessage(
            String sender, String senderUserId,
            String[] lines,
            @Nullable TextColor nameColor,
            @Nullable Attachment[] attachments,
            @Nullable ChatRelayIntegration.ChatMessage replyingTo
    ) {
        private static final int SUMMARY_LENGTH = 40;

        MutableText getSenderName() {
            MutableText sender = Text.literal(this.sender);
            if (this.nameColor != null) {
                var style = sender.getStyle()
                        .withColor(this.nameColor)
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal(this.senderUserId)));
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
        private static final MutableText NEW_LINE = Text.literal("\n | ").formatted(Formatting.GRAY);

        MutableText text;
        boolean first = true;

        MessageBuilder(Text prefix) {
            this.text = Text.empty().append(prefix).append(ScreenTexts.SPACE);
        }

        void append(MutableText text) {
            if (this.first) {
                this.text = this.text.append(text);
                this.first = false;
            } else {
                this.text = this.text.append(NEW_LINE).append(text);
            }
        }

        MutableText build() {
            return this.text;
        }
    }
}
