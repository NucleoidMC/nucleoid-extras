package xyz.nucleoid.extras.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SkinEncoder {
    /**
     * @param hash a hash part of the skin URL after <a href="http://textures.minecraft.net/texture/">http://textures.minecraft.net/texture/</a>
     * @return Base64-encoded skin texture for player heads
     */
    public static String encode(String hash) {
        if (hash == null) return null;
        if (hash.length() > 80) {
            return hash;
        }

        return encodeUrl("http://textures.minecraft.net/texture/" + hash);
    }

    /**
     * @return Base64-encoded skin texture for player heads
     */
    public static String encodeUrl(String url) {
        if (url == null) return null;
        var value = """
            {"textures":{"SKIN":{"url":"%s"}}}""".formatted(url);
        return new String(Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
