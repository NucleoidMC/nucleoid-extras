package xyz.nucleoid.extras.integrations.http;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.util.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RankingsEntry(int ranking, double value) {
    public static Codec<Map<Identifier, RankingsEntry>> CODEC = Codec.unboundedMap(Identifier.CODEC, Codec.list(Codec.DOUBLE).xmap(x -> new RankingsEntry(x.get(0).intValue(), x.get(1)), x -> List.of((double) x.ranking, x.value)));
}
