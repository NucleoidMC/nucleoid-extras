package xyz.nucleoid.extras.chat_filter;

import com.google.common.base.Splitter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class ChatFilterConfig {
    private static final Codec<String> WORD_CODEC = Codec.STRING.xmap(s -> s.toLowerCase(Locale.ROOT), s -> s.toLowerCase(Locale.ROOT));
    private static final Codec<Set<String>> WORD_SET_CODEC = WORD_CODEC.listOf().xmap(Set::copyOf, List::copyOf);

    public static final Codec<ChatFilterConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
        WORD_SET_CODEC.optionalFieldOf("illegal_words", Set.of()).forGetter(c -> c.illegalWords),
        WORD_CODEC.listOf().optionalFieldOf("contains_illegal_text", List.of()).forGetter(c -> c.containsIllegalText),
        PlasmidCodecs.TEXT.optionalFieldOf("feedback_message").forGetter(c -> Optional.ofNullable(c.feedbackMessage)),
        BuiltInRegistries.SOUND_EVENT.holderByNameCodec().optionalFieldOf("feedback_sound").forGetter(c -> Optional.ofNullable(c.feedbackSound))
    ).apply(i, ChatFilterConfig::new));

    private static final Splitter WORD_SPLITTER = Splitter.onPattern("\\W");

    private final Set<String> illegalWords;
    private final List<String> containsIllegalText;
    private final @Nullable Component feedbackMessage;
    private final @Nullable Holder<SoundEvent> feedbackSound;

    private ChatFilterConfig(Set<String> illegalWords, List<String> containsIllegalText, Optional<Component> feedbackMessage, Optional<Holder<SoundEvent>> feedbackSound) {
        this.illegalWords = illegalWords;
        this.containsIllegalText = containsIllegalText;

        this.feedbackMessage = feedbackMessage.map(ChatFilterConfig::formatFeedback).orElse(null);
        this.feedbackSound = feedbackSound.orElse(null);
    }

    private static MutableComponent formatFeedback(Component text) {
        return ComponentUtils.mergeStyles(text.copy(), Style.EMPTY.withColor(ChatFormatting.RED));
    }

    public boolean test(String message) {
        message = message.toLowerCase(Locale.ROOT);

        for (var text : this.containsIllegalText) {
            if (message.contains(text)) {
                return true;
            }
        }

        var illegalWords = this.illegalWords;
        for (var word : WORD_SPLITTER.split(message)) {
            if (illegalWords.contains(word)) {
                return true;
            }
        }

        return false;
    }

    public void sendFeedbackTo(ServerPlayer player) {
        if (this.feedbackMessage != null) {
            player.sendSystemMessage(this.feedbackMessage, true);
        }

        if (this.feedbackSound != null) {
            player.connection.send(new ClientboundSoundPacket(this.feedbackSound, SoundSource.MASTER, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f, player.getRandom().nextLong()));
        }
    }
}
