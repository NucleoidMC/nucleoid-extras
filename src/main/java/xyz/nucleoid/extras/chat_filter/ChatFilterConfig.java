package xyz.nucleoid.extras.chat_filter;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.codecs.MoreCodecs;

import java.util.*;
import java.util.stream.Collectors;

public final class ChatFilterConfig {
    public static final Codec<ChatFilterConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.listOf().optionalFieldOf("illegal_words", ImmutableList.of()).forGetter(c -> new ArrayList<>(c.illegalWords)),
                Codec.STRING.listOf().optionalFieldOf("contains_illegal_text", ImmutableList.of()).forGetter(c -> c.containsIllegalText),
                MoreCodecs.TEXT.optionalFieldOf("feedback_message").forGetter(c -> Optional.ofNullable(c.feedbackMessage)),
                Registry.SOUND_EVENT.optionalFieldOf("feedback_sound").forGetter(c -> Optional.ofNullable(c.feedbackSound))
        ).apply(instance, ChatFilterConfig::new);
    });

    private final Set<String> illegalWords;
    private final List<String> containsIllegalText;
    private final @Nullable MutableText feedbackMessage;
    private final @Nullable SoundEvent feedbackSound;

    private ChatFilterConfig(List<String> illegalWords, List<String> containsIllegalText, Optional<MutableText> feedbackMessage, Optional<SoundEvent> feedbackSound) {
        this.illegalWords = illegalWords.stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        this.containsIllegalText = containsIllegalText.stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        this.feedbackMessage = feedbackMessage.map(ChatFilterConfig::formatFeedback).orElse(null);
        this.feedbackSound = feedbackSound.orElse(null);
    }

    private static MutableText formatFeedback(MutableText text) {
        return text.shallowCopy().styled(style -> {
            if (style.getColor() == null) {
                return style.withColor(Formatting.RED);
            } else {
                return style;
            }
        });
    }

    public boolean test(String message) {
        message = message.toLowerCase(Locale.ROOT);

        for (String text : this.containsIllegalText) {
            if (message.contains(text)) {
                return true;
            }
        }

        String[] words = message.split("\\s");

        Set<String> illegalWords = this.illegalWords;
        for (String word : words) {
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
            player.playSound(this.feedbackSound, SoundCategory.MASTER, 1.0F, 1.0F);
        }
    }
}
