package xyz.nucleoid.extras.data.provider;

import com.google.common.hash.HashCode;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.model.NEModels;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class NEModelProvider implements DataProvider {
    private static final String NAME = NucleoidExtras.identifier("model_provider").toString();

    private final DataOutput output;
    private final HashMap<Identifier, ItemAsset> assetMap;
    private final HashMap<Identifier, ModelAsset> modelMap;

    public NEModelProvider(FabricDataOutput output) {
        this.output = output;
        this.assetMap = new HashMap<>();
        this.modelMap = new HashMap<>();
    }

    public void runWriters(BiConsumer<String, byte[]> assetWriter) {
        createItems();
        this.assetMap.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemAsset(id), asset.toBytes()));
        this.modelMap.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemModel(id), asset.toBytes()));
    }

    private void createItems() {
        spriteItem(NEModels.CONTROLLER);
    }

    private void spriteItem(Identifier id) {
        this.assetMap.put(id, new ItemAsset(new BasicItemModel(id.withPrefixedPath("item/")), ItemAsset.Properties.DEFAULT));
        this.modelMap.put(id, ModelAsset.builder().parent(Identifier.of("item/generated")).texture("layer0", id.withPrefixedPath("item/").toString()).build());
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                writer.write(this.output.getPath().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            this.
                runWriters(assetWriter);
        }, Util.getMainWorkerExecutor());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
