package xyz.nucleoid.extras.component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public record TaterPositionsComponent(SetMultimap<RegistryEntry<Item>, BlockPos> positions) implements PolymerComponent {
    private static final Codec<Map<RegistryEntry<Item>, List<BlockPos>>> MAP_CODEC = Codec.unboundedMap(Registries.ITEM.getEntryCodec(), BlockPos.CODEC.listOf());
    private static final Codec<SetMultimap<RegistryEntry<Item>, BlockPos>> MULTIMAP_CODEC = MAP_CODEC.xmap(TaterPositionsComponent::toMultimap, TaterPositionsComponent::toMap);

    public static final TaterPositionsComponent DEFAULT = new TaterPositionsComponent(ImmutableSetMultimap.of());

    public static final Codec<TaterPositionsComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                MULTIMAP_CODEC.optionalFieldOf("positions", DEFAULT.positions).forGetter(TaterPositionsComponent::positions)
        ).apply(instance, TaterPositionsComponent::new)
    );

    private static <K, V> SetMultimap<K, V> toMultimap(Map<K, List<V>> map) {
        var multimap = ImmutableSetMultimap.<K, V>builder();

        for (var entry : map.entrySet()) {
            multimap.putAll(entry.getKey(), entry.getValue());
        }

        return multimap.build();
    }

    private static <K, V> Map<K, List<V>> toMap(SetMultimap<K, V> multimap) {
        var map = ImmutableMap.<K, List<V>>builder();

        for (var entry : multimap.asMap().entrySet()) {
            map.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        return map.build();
    }
}
