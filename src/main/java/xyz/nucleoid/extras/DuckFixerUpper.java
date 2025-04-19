package xyz.nucleoid.extras;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;

public class DuckFixerUpper implements ModInitializer {
	private static final int DATA_VERSION_1_20_4 = 3700;
	private static final int DATA_VERSION_1_21_3 = 4082;

	private static final IndentDetector[] INDENT_DETECTORS = {
		new IndentDetector("^  \\S", "  "),
		new IndentDetector("^    \\S", "    "),
		new IndentDetector("^\\t\\S", "\t"),
	};

	private static final String MOD_ID = "duckfixerupper";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		boolean success = execute();
		System.exit(success ? 0 : 1);
	}

	public static boolean execute() {
		LOGGER.info("Quack!");

		Path dir = FabricLoader.getInstance().getGameDir();

		Path input = dir.resolve("dfu-input");
		input.toFile().mkdir();

		Path output = dir.resolve("dfu-output");
		output.toFile().mkdir();

		Collection<File> files = FileUtils.listFiles(input.toFile(), null, true);

		if (files == null || files.isEmpty()) {
			LOGGER.info("No files found in input directory");
			return false;
		}

		int successes = 0;

		for (File file : files) {
			Path path = file.toPath();

			Path relative = input.relativize(path);
			String name = relative.toString();

			Path outputPath = output.resolve(relative);
			outputPath.toFile().getParentFile().mkdirs();

			try {
				if (!file.getName().endsWith(".json")) {
					LOGGER.info("Skipping non-JSON file: {}", name);

					Files.copy(path, outputPath, StandardCopyOption.REPLACE_EXISTING);
					continue;
				} else if (!Files.isRegularFile(path)) {
					LOGGER.info("Skipping non-file: {}", name);

					Files.copy(path, outputPath, StandardCopyOption.REPLACE_EXISTING);
					continue;
				}

				try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
					String contents = Files.readString(path);
					String indent = getIndent(contents);

					try (JsonReader jsonReader = new JsonReader(reader)) {
						JsonElement inputJson = Streams.parse(jsonReader);
						JsonElement outputJson = upgrade(inputJson);

						if (inputJson.equals(outputJson)) {
							LOGGER.info("Not reformatting unchanged file: {}", name);

							Files.copy(path, outputPath, StandardCopyOption.REPLACE_EXISTING);
							continue;
						}

						try (Writer writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
							try (JsonWriter jsonWriter = new JsonWriter(writer)) {
								if (indent != null) {
									jsonWriter.setIndent(indent);
								}

								Streams.write(outputJson, jsonWriter);

								if (contents.endsWith("\n")) {
									writer.write("\n");
								}

								successes += 1;
							}
						}
					}
				}
			} catch (IOException exception) {
				LOGGER.error("Failed to handle file: {}", name, exception);
			}
		}

		LOGGER.info("Successfully upgraded {} files", successes);
		return true;
	}

	private static JsonElement upgrade(JsonElement input) {
		if (input.isJsonObject()) {
			JsonObject output = input.deepCopy().getAsJsonObject();

			if (output.has("id") && output.has("Count")) {
				Dynamic<JsonElement> upgradedOutput = Schemas.getFixer().update(TypeReferences.ITEM_STACK, new Dynamic<>(JsonOps.INSTANCE, output), DATA_VERSION_1_20_4, DATA_VERSION_1_21_3);
				return upgradedOutput.getValue();
			} else {
				for (String key : output.keySet()) {
					JsonElement value = output.get(key);
					output.add(key, upgrade(value));
				}

				return output;
			}
		} else if (input.isJsonArray()) {
			JsonArray output = new JsonArray();

			for (JsonElement element : input.getAsJsonArray()) {
				output.add(upgrade(element));
			}

			return output;
		}

		return input;
	}

	private static String getIndent(String file) {
		for (IndentDetector detector : INDENT_DETECTORS) {
			if (detector.matches(file)) {
				return detector.indent();
			}
		}

		return null;
	}

	record IndentDetector(Pattern pattern, String indent) {
		public IndentDetector(String pattern, String indent) {
			this(Pattern.compile(pattern, Pattern.MULTILINE), indent);
		}

		public boolean matches(String file) {
			return this.pattern.matcher(file).find();
		}
	}
}
