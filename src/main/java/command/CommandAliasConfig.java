package command;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.Map;

public final class CommandAliasConfig {
    public static final Codec<CommandAliasConfig> CODEC = Codec.unboundedMap(
            Codec.STRING,
            Codec.either(Codec.STRING, Codec.STRING.listOf())
                    .xmap(
                            either -> either.map(
                                    value -> new String[] { value },
                                    strings -> strings.toArray(new String[0])
                            ),
                            array -> Either.right(Arrays.asList(array))
                    )
    ).xmap(CommandAliasConfig::new, config -> config.map);

    private final Map<String, String[]> map;

    private CommandAliasConfig(Map<String, String[]> map) {
        this.map = map;
    }

    public Map<String, String[]> getMap() {
        return this.map;
    }
}
