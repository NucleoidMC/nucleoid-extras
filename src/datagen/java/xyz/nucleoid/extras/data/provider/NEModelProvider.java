package xyz.nucleoid.extras.data.provider;

import com.google.common.hash.HashCode;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelTransformation;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.model.NEModels;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class NEModelProvider implements DataProvider {
    private static final String NAME = NucleoidExtras.identifier("model_provider").toString();

    private final PackOutput output;
    private final HashMap<Identifier, ItemAsset> assetMap;
    private final HashMap<Identifier, ModelAsset> modelMap;

    public NEModelProvider(FabricPackOutput output) {
        this.output = output;
        this.assetMap = new HashMap<>();
        this.modelMap = new HashMap<>();
    }

    private void createItems() {
        spriteItem(NEModels.CONTROLLER);
    }

    private void spriteItem(Identifier id) {
        this.assetMap.put(id, new ItemAsset(new BasicItemModel(id.withPrefix("item/")), ItemAsset.Properties.DEFAULT));
        this.modelMap.put(id, ModelAsset.builder()
                .parent(Identifier.withDefaultNamespace("item/generated"))
                .texture("layer0", id.withPrefix("item/"))
                        .transformation(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new ModelTransformation(
                                new Vec3(-15, 0, 0),
                                new Vec3(-9, 3.2, 1.13),
                                new Vec3(0.68, 0.68, 0.68)
                        ))
                .build());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                cache.writeIfNeeded(this.output.getOutputFolder().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            this.
                runWriters(assetWriter);
        }, Util.ioPool());
    }

    public void runWriters(BiConsumer<String, byte[]> assetWriter) {
        createItems();
        this.assetMap.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemAsset(id), asset.toBytes()));
        this.modelMap.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemModel(id), asset.toBytes()));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
