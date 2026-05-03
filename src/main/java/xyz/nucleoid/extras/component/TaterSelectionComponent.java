package xyz.nucleoid.extras.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public record TaterSelectionComponent(Optional<Holder<Block>> tater, boolean allowViralCollection) implements PolymerComponent {
    public static final TaterSelectionComponent DEFAULT = new TaterSelectionComponent(Optional.empty(), true);

    public static final Codec<TaterSelectionComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                BuiltInRegistries.BLOCK.holderByNameCodec().optionalFieldOf("tater").forGetter(TaterSelectionComponent::tater),
                Codec.BOOL.optionalFieldOf("allow_viral_collection", DEFAULT.allowViralCollection).forGetter(TaterSelectionComponent::allowViralCollection)
        ).apply(instance, TaterSelectionComponent::new)
    );

    public TaterSelectionComponent selected(Holder<Block> tater) {
        return new TaterSelectionComponent(Optional.ofNullable(tater), this.allowViralCollection);
    }
}
