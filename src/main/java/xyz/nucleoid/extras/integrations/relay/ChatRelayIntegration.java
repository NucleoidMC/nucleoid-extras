package xyz.nucleoid.extras.integrations.relay;

import com.google.gson.JsonObject;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledchat.StyledChatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.integrations.IntegrationSender;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent;

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

            ServerTickEvents.END_SERVER_TICK.register(integration::tick);

            if (FabricLoader.getInstance().isModLoaded("styledchat")) {
                StyledChatEvents.MESSAGE_CONTENT.register((message, context) -> {
                    if (context.hasPlayer()) {
                        var text = message.toText(ParserContext.of(PlaceholderContext.KEY, context), true);
                        var player = context.player();
                        assert player != null;
                        var sender = player.asMessageSender();
                        integration.onSendChatMessage(sender, text.getString());
                    }
                    return message;
                });
            } else {
                Stimuli.global().listen(PlayerChatEvent.EVENT, (sender, message) -> {
                    integration.onSendChatMessage(sender, message.getContent().getString());
                    return ActionResult.PASS;
                });
            }
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
        var name = user.get("name").getAsString();
        int discriminator = user.get("discriminator").getAsInt();
        return name + "#" + discriminator;
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

    private void onSendChatMessage(MessageSender sender, String content) {
        var body = new JsonObject();

        var senderRoot = new JsonObject();
        senderRoot.addProperty("id", sender.uuid().toString());
        senderRoot.addProperty("name", sender.name().getString());

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
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.url))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Open attachment")))
                ));
            }
        }

        playerManager.broadcast(result.build(), MessageType.SYSTEM);
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
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(this.senderUserId)));
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
            this.text = Text.empty().append(prefix).append(" ");
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
