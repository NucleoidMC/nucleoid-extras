package net.minecraft.core;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.core.registries.codec.RegistryFileCodec;
import net.minecraft.core.registries.codec.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

/// Class to bridge 26.2 usage of this
@Deprecated(forRemoval = true)
public class RegistryCodecs {
    public static <E> Codec<HolderSet<E>> homogeneousList(final ResourceKey<? extends Registry<E>> registryKey, final Codec<E> elementCodec) {
        return homogeneousList(registryKey, elementCodec, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(
        final ResourceKey<? extends Registry<E>> registryKey, final Codec<E> elementCodec, final boolean alwaysUseList
    ) {
        return HolderSetCodec.create(registryKey, RegistryFileCodec.create(registryKey, elementCodec, false), alwaysUseList);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(final ResourceKey<? extends Registry<E>> registryKey) {
        return homogeneousList(registryKey, false);
    }

    public static <E> Codec<HolderSet<E>> homogeneousList(final ResourceKey<? extends Registry<E>> registryKey, final boolean alwaysUseList) {
        return HolderSetCodec.create(registryKey, RegistryFixedCodec.create(registryKey), alwaysUseList);
    }
}
