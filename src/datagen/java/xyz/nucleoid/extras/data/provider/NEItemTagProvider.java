package xyz.nucleoid.extras.data.provider;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

public class NEItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public NEItemTagProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries, FabricTagsProvider.BlockTagsProvider blockTags) {
        super(dataOutput, registries, blockTags);
    }

    @Override
    protected void addTags(Provider lookup) {
        this.copy(BlockTags.DOORS, ItemTags.DOORS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
    }
}
