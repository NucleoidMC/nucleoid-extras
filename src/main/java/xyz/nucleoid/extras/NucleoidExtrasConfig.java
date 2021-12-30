package xyz.nucleoid.extras;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.chat_filter.ChatFilterConfig;
import xyz.nucleoid.extras.command.CommandAliasConfig;
import xyz.nucleoid.extras.error.ErrorReportingConfig;
import xyz.nucleoid.extras.integrations.IntegrationsConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public record NucleoidExtrasConfig(
        boolean sidebar,
        @Nullable IntegrationsConfig integrations,
        @Nullable CommandAliasConfig aliases,
        @Nullable ChatFilterConfig chatFilter,
        ErrorReportingConfig errorReporting,
        boolean devServer
) {
    private static final Path PATH = Paths.get("config/nucleoid.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Codec<NucleoidExtrasConfig> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.BOOL.optionalFieldOf("sidebar", false).forGetter(NucleoidExtrasConfig::sidebar),
                IntegrationsConfig.CODEC.optionalFieldOf("integrations").forGetter(config -> Optional.ofNullable(config.integrations())),
                CommandAliasConfig.CODEC.optionalFieldOf("aliases").forGetter(config -> Optional.ofNullable(config.aliases())),
                ChatFilterConfig.CODEC.optionalFieldOf("chat_filter").forGetter(config -> Optional.ofNullable(config.chatFilter())),
                ErrorReportingConfig.CODEC.optionalFieldOf("error_reporting", ErrorReportingConfig.NONE).forGetter(NucleoidExtrasConfig::errorReporting),
                Codec.BOOL.optionalFieldOf("devServer", false).forGetter(NucleoidExtrasConfig::devServer)
        ).apply(instance, (sidebar, integrations, aliases, filter, errorReporting, devServer) ->
            new NucleoidExtrasConfig(sidebar, integrations.orElse(null), aliases.orElse(null), filter.orElse(null), errorReporting, devServer)
        )
    );

    private static NucleoidExtrasConfig instance;

    private NucleoidExtrasConfig() {
        this(false, null, null, null, ErrorReportingConfig.NONE, false);
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
        try (var input = Files.newInputStream(PATH)) {
            var json = JsonParser.parseReader(new InputStreamReader(input));
            var result = CODEC.decode(JsonOps.INSTANCE, json).map(Pair::getFirst);
            return result.result().orElseGet(NucleoidExtrasConfig::new);
        } catch (IOException e) {
            NucleoidExtras.LOGGER.warn("Failed to load nucleoid extras config", e);
            return new NucleoidExtrasConfig();
        }
    }

    private static NucleoidExtrasConfig createDefaultConfig() {
        var config = new NucleoidExtrasConfig();
        try (var output = Files.newOutputStream(PATH)) {
            var result = CODEC.encodeStart(JsonOps.INSTANCE, config).result();
            if (result.isPresent()) {
                var json = result.get();
                IOUtils.write(GSON.toJson(json), output, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            NucleoidExtras.LOGGER.warn("Failed to create default plasmid config", e);
        }
        return config;
    }
}
