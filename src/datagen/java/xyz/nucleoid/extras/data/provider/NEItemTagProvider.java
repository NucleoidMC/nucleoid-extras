package xyz.nucleoid.extras.data.provider;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

public class NEItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public NEItemTagProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries, FabricTagProvider.BlockTagProvider blockTags) {
        super(dataOutput, registries, blockTags);
    }

    @Override
    protected void addTags(Provider lookup) {
        this.copy(BlockTags.DOORS, ItemTags.DOORS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
    }
}
