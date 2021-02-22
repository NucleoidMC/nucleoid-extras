package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.event.PlayerSendChatEvent;
import xyz.nucleoid.plasmid.chat.ChatChannel;
import xyz.nucleoid.plasmid.chat.HasChatChannel;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

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

        ChatMessage replyingTo;
        if (body.has("replying_to")) {
            replyingTo = parseMessage(body.getAsJsonObject("replying_to"));
        } else {
            replyingTo = null;
        }

        return new ChatMessage(sender, content.split("\n"), nameColor, attachments, replyingTo);
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
        ManagedGameSpace gameSpace = ManagedGameSpace.forWorld(player.world);
        boolean teamChatAllowed = gameSpace != null && gameSpace.testRule(GameRule.TEAM_CHAT) == RuleResult.ALLOW;
        if (((HasChatChannel) player).getChatChannel() == ChatChannel.TEAM
                && player.getScoreboardTeam() == null && teamChatAllowed) {
            return;
        }

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
        PlayerManager playerManager = server.getPlayerManager();

        MutableText sender = message.getSenderName();
        MutableText prefix = new LiteralText("<@").append(sender).append(">").formatted(Formatting.GRAY);
        MessageBuilder result = new MessageBuilder(prefix);

        if (message.replyingTo != null) {
            result.append(this.createReplyText(message.replyingTo));
        }

        for (String line : message.lines) {
            result.append(new LiteralText(line));
        }

        if (message.attachments != null) {
            for (Attachment attachment : message.attachments) {
                result.append(new LiteralText("[Attachment: " + attachment.name + "]").styled(style -> {
                    return style.withFormatting(Formatting.BLUE, Formatting.UNDERLINE)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.url))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open attachment")));
                }));
            }
        }

        playerManager.broadcastChatMessage(result.build(), MessageType.CHAT, Util.NIL_UUID);
    }

    private MutableText createReplyText(ChatMessage message) {
        String summary = message.getSummary();

        MutableText replyText = new LiteralText("(replying to @")
                .append(message.getSenderName());

        if (summary != null) {
            replyText = replyText.append(": ").append(new LiteralText(summary).formatted(Formatting.ITALIC));
        }

        replyText = replyText.append(")");

        return replyText.formatted(Formatting.GRAY);
    }

    static class ChatMessage {
        private static final int SUMMARY_LENGTH = 40;

        final String sender;
        final String[] lines;
        final TextColor nameColor;
        final Attachment[] attachments;
        final ChatMessage replyingTo;

        ChatMessage(String sender, String[] lines, @Nullable TextColor nameColor, @Nullable Attachment[] attachments, @Nullable ChatMessage replyingTo) {
            this.sender = sender;
            this.lines = lines;
            this.nameColor = nameColor;
            this.attachments = attachments;
            this.replyingTo = replyingTo;
        }

        MutableText getSenderName() {
            MutableText sender = new LiteralText(this.sender);
            if (this.nameColor != null) {
                sender = sender.setStyle(sender.getStyle().withColor(this.nameColor));
            }
            return sender;
        }

        @Nullable
        String getSummary() {
            if (this.lines.length > 0) {
                String line = this.lines[0];
                if (line.length() <= SUMMARY_LENGTH) {
                    return line;
                } else {
                    return line.substring(0, SUMMARY_LENGTH - 2) + "..";
                }
            }
            return null;
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
