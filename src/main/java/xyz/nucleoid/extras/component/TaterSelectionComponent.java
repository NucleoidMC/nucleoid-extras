package xyz.nucleoid.extras.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;

public record TaterSelectionComponent(Optional<RegistryEntry<Block>> tater, boolean allowViralCollection) implements PolymerComponent {
    public static final TaterSelectionComponent DEFAULT = new TaterSelectionComponent(Optional.empty(), true);

    public static final Codec<TaterSelectionComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Registries.BLOCK.getEntryCodec().optionalFieldOf("tater").forGetter(TaterSelectionComponent::tater),
                Codec.BOOL.optionalFieldOf("allow_viral_collection", DEFAULT.allowViralCollection).forGetter(TaterSelectionComponent::allowViralCollection)
        ).apply(instance, TaterSelectionComponent::new)
    );

    public TaterSelectionComponent selected(RegistryEntry<Block> tater) {
        return new TaterSelectionComponent(Optional.ofNullable(tater), this.allowViralCollection);
    }
}
