package xyz.nucleoid.extras.util;

import com.mojang.serialization.Codec;

import java.net.URI;
import java.util.UUID;

public class ExtraCodecs {
    public static final Codec<UUID> STRING_UUID = Codec.STRING.xmap(UUID::fromString, UUID::toString);
    public static final Codec<URI> URI = Codec.STRING.xmap(java.net.URI::create, x -> x.toString());
}
