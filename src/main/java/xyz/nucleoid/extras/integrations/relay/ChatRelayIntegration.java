package xyz.nucleoid.extras.integrations.relay;

import com.google.gson.JsonObject;
import eu.pb4.styledchat.StyledChatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtras;
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
                StyledChatEvents.MESSAGE_CONTENT_SEND.register((message, sender, filtered) -> {
                    if (!filtered) {
                        integration.onSendChatMessage(sender, message.getString());
                    }
                    return message;
                });
            } else {
                Stimuli.global().listen(PlayerChatEvent.EVENT, (sender, message) -> {
                    var content = NucleoidExtras.getChatMessageContent(message);
                    integration.onSendChatMessage(sender, content);
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

    private ActionResult onSendChatMessage(ServerPlayerEntity player, String content) {
        var body = new JsonObject();

        var profile = player.getGameProfile();

        var senderRoot = new JsonObject();
        senderRoot.addProperty("id", profile.getId().toString());
        senderRoot.addProperty("name", profile.getName());

        body.add("sender", senderRoot);
        body.addProperty("content", content);

        this.chatSender.send(body);

        return ActionResult.PASS;
    }

    private void broadcastMessage(MinecraftServer server, ChatMessage message) {
        var playerManager = server.getPlayerManager();

        var sender = message.getSenderName();
        var prefix = new LiteralText("<@").append(sender).append(">").formatted(Formatting.GRAY);
        var result = new MessageBuilder(prefix);

        if (message.replyingTo != null) {
            result.append(this.createReplyText(message.replyingTo));
        }

        for (var line : message.lines) {
            result.append(new LiteralText(line));
        }

        if (message.attachments != null) {
            for (var attachment : message.attachments) {
                result.append(new LiteralText("[Attachment: " + attachment.name + "]").styled(style -> {
                    return style.withFormatting(Formatting.BLUE, Formatting.UNDERLINE)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.url))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open attachment")));
                }));
            }
        }

        playerManager.broadcast(result.build(), MessageType.CHAT, Util.NIL_UUID);
    }

    private MutableText createReplyText(ChatMessage message) {
        var summary = message.getSummary();

        var replyText = new LiteralText("(replying to @")
                .append(message.getSenderName());

        if (summary != null) {
            replyText = replyText.append(": ").append(new LiteralText(summary).formatted(Formatting.ITALIC));
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
            MutableText sender = new LiteralText(this.sender);
            if (this.nameColor != null) {
                var style = sender.getStyle()
                        .withColor(this.nameColor)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.senderUserId)));
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
        private static final MutableText NEW_LINE = new LiteralText("\n | ").formatted(Formatting.GRAY);

        MutableText text;
        boolean first = true;

        MessageBuilder(Text prefix) {
            this.text = new LiteralText("").append(prefix).append(" ");
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
