package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;

@Deprecated(forRemoval = true)
public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {
   private final Codec<Holder<E>> codec;

   public static <E> RegistryFixedCodec<E> create(final ResourceKey<? extends Registry<E>> registryKey) {
      return new RegistryFixedCodec<>(registryKey);
   }

   private RegistryFixedCodec(final ResourceKey<? extends Registry<E>> registryKey) {
      this.codec = net.minecraft.core.registries.codec.RegistryFixedCodec.create(registryKey);
   }

    public <T> DataResult<T> encode(final Holder<E> input, final DynamicOps<T> ops, final T prefix) {
        return this.codec.encode(input, ops, prefix);
    }

    public <T> DataResult<Pair<Holder<E>, T>> decode(final DynamicOps<T> ops, final T input) {
        return this.codec.decode(ops, input);
    }

    public String toString() {
        return "Legacy[" + this.codec + "]";
    }
}
