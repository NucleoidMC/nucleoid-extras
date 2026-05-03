package xyz.nucleoid.extras.integrations.http;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.extras.util.ExtraCodecs;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public record RankingsEntry(int ranking, double value) {
    public static Codec<Map<ResourceLocation, RankingsEntry>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.list(Codec.DOUBLE).xmap(x -> new RankingsEntry(x.get(0).intValue(), x.get(1)), x -> List.of((double) x.ranking, x.value)));
}
