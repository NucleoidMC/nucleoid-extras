package xyz.nucleoid.extras.chat_filter;

import com.google.common.base.Splitter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import org.jetbrains.annotations.Nullable;

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
        Registries.SOUND_EVENT.getEntryCodec().optionalFieldOf("feedback_sound").forGetter(c -> Optional.ofNullable(c.feedbackSound))
    ).apply(i, ChatFilterConfig::new));

    private static final Splitter WORD_SPLITTER = Splitter.onPattern("\\W");

    private final Set<String> illegalWords;
    private final List<String> containsIllegalText;
    private final @Nullable Text feedbackMessage;
    private final @Nullable RegistryEntry<SoundEvent> feedbackSound;

    private ChatFilterConfig(Set<String> illegalWords, List<String> containsIllegalText, Optional<Text> feedbackMessage, Optional<RegistryEntry<SoundEvent>> feedbackSound) {
        this.illegalWords = illegalWords;
        this.containsIllegalText = containsIllegalText;

        this.feedbackMessage = feedbackMessage.map(ChatFilterConfig::formatFeedback).orElse(null);
        this.feedbackSound = feedbackSound.orElse(null);
    }

    private static MutableText formatFeedback(Text text) {
        return Texts.setStyleIfAbsent(text.copy(), Style.EMPTY.withColor(Formatting.RED));
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

    public void sendFeedbackTo(ServerPlayerEntity player) {
        if (this.feedbackMessage != null) {
            player.sendMessage(this.feedbackMessage, true);
        }

        if (this.feedbackSound != null) {
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(this.feedbackSound, SoundCategory.MASTER, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f, player.getRandom().nextLong()));
        }
    }
}
