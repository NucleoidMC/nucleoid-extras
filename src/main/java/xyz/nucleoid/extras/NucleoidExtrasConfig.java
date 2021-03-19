package xyz.nucleoid.extras;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.chat_filter.ChatFilterConfig;
import xyz.nucleoid.extras.command.CommandAliasConfig;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class NucleoidExtrasConfig {
    private static final Path PATH = Paths.get("config/nucleoid.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final Codec<NucleoidExtrasConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.BOOL.optionalFieldOf("sidebar", false).forGetter(config -> config.sidebar),
                IntegrationsConfig.CODEC.optionalFieldOf("integrations").forGetter(config -> Optional.ofNullable(config.integrations)),
                CommandAliasConfig.CODEC.optionalFieldOf("aliases").forGetter(config -> Optional.ofNullable(config.aliases)),
                ChatFilterConfig.CODEC.optionalFieldOf("chat_filter").forGetter(config -> Optional.ofNullable(config.chatFilter))
        ).apply(instance, NucleoidExtrasConfig::new);
    });

    private static NucleoidExtrasConfig instance;

    private final boolean sidebar;
    private final IntegrationsConfig integrations;
    private final CommandAliasConfig aliases;
    private final ChatFilterConfig chatFilter;

    private NucleoidExtrasConfig(boolean sidebar, Optional<IntegrationsConfig> integrations, Optional<CommandAliasConfig> aliases, Optional<ChatFilterConfig> chatFilter) {
        this.sidebar = sidebar;
        this.integrations = integrations.orElse(null);
        this.aliases = aliases.orElse(null);
        this.chatFilter = chatFilter.orElse(null);
    }

    private NucleoidExtrasConfig() {
        this.sidebar = false;
        this.integrations = null;
        this.aliases = null;
        this.chatFilter = null;
    }

    @Nullable
    public IntegrationsConfig getIntegrations() {
        return this.integrations;
    }

    @Nullable
    public CommandAliasConfig getAliases() {
        return this.aliases;
    }

    @Nullable
    public ChatFilterConfig getChatFilter() {
        return this.chatFilter;
    }

    public boolean isSidebarEnabled() {
        return this.sidebar;
    }

    @NotNull
    public static NucleoidExtrasConfig get() {
        if (instance == null) {
            instance = initializeConfig();
        }
        return instance;
    }

    private static NucleoidExtrasConfig initializeConfig() {
        if (Files.exists(PATH)) {
            return loadConfig();
        } else {
            return createDefaultConfig();
        }
    }

    private static NucleoidExtrasConfig loadConfig() {
        try (InputStream input = Files.newInputStream(PATH)) {
            JsonElement json = JSON_PARSER.parse(new InputStreamReader(input));
            DataResult<NucleoidExtrasConfig> result = CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
            return result.result().orElseGet(NucleoidExtrasConfig::new);
        } catch (IOException e) {
            NucleoidExtras.LOGGER.warn("Failed to load nucleoid extras config", e);
            return new NucleoidExtrasConfig();
        }
    }

    private static NucleoidExtrasConfig createDefaultConfig() {
        NucleoidExtrasConfig config = new NucleoidExtrasConfig();
        try (OutputStream output = Files.newOutputStream(PATH)) {
            Optional<JsonElement> result = CODEC.encodeStart(JsonOps.INSTANCE, config).result();
            if (result.isPresent()) {
                JsonElement json = result.get();
                IOUtils.write(GSON.toJson(json), output, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            NucleoidExtras.LOGGER.warn("Failed to create default plasmid config", e);
        }
        return config;
    }
}
