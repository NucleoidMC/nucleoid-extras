package xyz.nucleoid.extras.command;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.codecs.MoreCodecs;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public final class CommandAliasConfig {
    public static final Codec<CommandAliasConfig> CODEC = Codec.unboundedMap(Codec.STRING, Entry.CODEC)
            .xmap(CommandAliasConfig::new, config -> config.map);

    private final Map<String, Entry> map;

    private CommandAliasConfig(Map<String, Entry> map) {
        this.map = map;
    }

    public Map<String, Entry> getMap() {
        return this.map;
    }

    public static final class Entry {
        private static final Codec<String[]> COMMANDS_CODEC = MoreCodecs.listOrUnit(Codec.STRING)
                .xmap(strings -> strings.toArray(new String[0]), Arrays::asList);

        private static final Codec<Entry> SIMPLE_CODEC = COMMANDS_CODEC
                .xmap(Entry::new, entry -> entry.commands);

        private static final Codec<Entry> RECORD_CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    COMMANDS_CODEC.fieldOf("execute").forGetter(entry -> entry.commands),
                    Codec.BOOL.optionalFieldOf("feedback", true).forGetter(entry -> entry.feedback)
            ).apply(instance, Entry::new);
        });

        public static final Codec<Entry> CODEC = Codec.either(RECORD_CODEC, SIMPLE_CODEC)
                .xmap(
                        either -> either.map(Function.identity(), Function.identity()),
                        entry -> entry.isSimple() ? Either.right(entry) : Either.left(entry)
                );

        public final String[] commands;
        public final boolean feedback;

        Entry(String[] commands) {
            this(commands, true);
        }

        Entry(String[] commands, boolean feedback) {
            this.commands = commands;
            this.feedback = feedback;
        }

        boolean isSimple() {
            return this.feedback;
        }
    }
}
