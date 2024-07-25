package xyz.nucleoid.extras.integrations.http;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.extras.util.ExtraCodecs;

import java.util.List;
import java.util.UUID;

public record LeaderboardEntry(UUID playerUuid, int ranking, double value) {
    public static Codec<List<LeaderboardEntry>> CODEC = Codec.list(RecordCodecBuilder.create(instance ->
        instance.group(
            ExtraCodecs.STRING_UUID.fieldOf("player").forGetter(LeaderboardEntry::playerUuid),
            Codec.INT.fieldOf("ranking").forGetter(LeaderboardEntry::ranking),
            Codec.DOUBLE.fieldOf("value").forGetter(LeaderboardEntry::value)
        ).apply(instance, LeaderboardEntry::new)
    ));
}
