package xyz.nucleoid.extras.chat_filter;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.*;
import java.util.stream.Collectors;

public final class ChatFilterConfig {
    public static final Codec<ChatFilterConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.STRING.listOf().optionalFieldOf("illegal_words", ImmutableList.of()).forGetter(c -> new ArrayList<>(c.illegalWords)),
                Codec.STRING.listOf().optionalFieldOf("contains_illegal_text", ImmutableList.of()).forGetter(c -> c.containsIllegalText),
                PlasmidCodecs.TEXT.optionalFieldOf("feedback_message").forGetter(c -> Optional.ofNullable(c.feedbackMessage)),
                Registries.SOUND_EVENT.createEntryCodec().optionalFieldOf("feedback_sound").forGetter(c -> Optional.ofNullable(c.feedbackSound))
        ).apply(instance, ChatFilterConfig::new)
    );

    private final Set<String> illegalWords;
    private final List<String> containsIllegalText;
    private final @Nullable Text feedbackMessage;
    private final @Nullable RegistryEntry<SoundEvent> feedbackSound;

    private ChatFilterConfig(List<String> illegalWords, List<String> containsIllegalText, Optional<Text> feedbackMessage, Optional<RegistryEntry<SoundEvent>> feedbackSound) {
        this.illegalWords = illegalWords.stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        this.containsIllegalText = containsIllegalText.stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        this.feedbackMessage = feedbackMessage.map(ChatFilterConfig::formatFeedback).orElse(null);
        this.feedbackSound = feedbackSound.orElse(null);
    }

    private static MutableText formatFeedback(Text text) {
        return text.copy().styled(style -> {
            if (style.getColor() == null) {
                return style.withColor(Formatting.RED);
            } else {
                return style;
            }
        });
    }

    public boolean test(String message) {
        message = message.toLowerCase(Locale.ROOT);

        for (var text : this.containsIllegalText) {
            if (message.contains(text)) {
                return true;
            }
        }

        var words = message.split("\\s");

        var illegalWords = this.illegalWords;
        for (var word : words) {
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
