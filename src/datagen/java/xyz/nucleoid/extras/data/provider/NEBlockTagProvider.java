package xyz.nucleoid.extras.data.provider;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.tags.BlockTags;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.tag.NEBlockTags;

public class NEBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public NEBlockTagProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(dataOutput, registries);
    }

    @Override
    protected void addTags(Provider lookup) {
        for (var block : TinyPotatoBlock.TATERS) {
            this.tag(NEBlockTags.COLLECTABLE_TATERS).add(block.builtInRegistryHolder().key());
        }

        var doors = this.tag(BlockTags.DOORS)
                .add(NEBlocks.TRANSIENT_IRON_DOOR.builtInRegistryHolder().key());
        NEBlocks.TRANSIENT_COPPER_DOOR.forEach(d -> doors.add(d.builtInRegistryHolder().key()));

        this.tag(NEBlockTags.LUCKY_TATER_DROPS)
                .add(NEBlocks.BRONZE_CAPSULE_TATER.builtInRegistryHolder().key())
                .add(NEBlocks.SILVER_CAPSULE_TATER.builtInRegistryHolder().key())
                .add(NEBlocks.GOLD_CAPSULE_TATER.builtInRegistryHolder().key());


        NEBlocks.TRANSIENT_COPPER_DOOR.forEach(d -> this.tag(BlockTags.MOB_INTERACTABLE_DOORS).add(d.builtInRegistryHolder().key()));


        this.tag(NEBlockTags.NON_VIBRATING_TATERS)
                .addOptionalTag(BlockTags.DAMPENS_VIBRATIONS)
                .add(NEBlocks.WARDEN_TATER.builtInRegistryHolder().key());

        this.tag(BlockTags.WOODEN_DOORS)
                .add(NEBlocks.TRANSIENT_OAK_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_SPRUCE_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_BIRCH_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_JUNGLE_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_ACACIA_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_CHERRY_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_DARK_OAK_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_MANGROVE_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_PALE_OAK_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_BAMBOO_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_CRIMSON_DOOR.builtInRegistryHolder().key())
                .add(NEBlocks.TRANSIENT_WARPED_DOOR.builtInRegistryHolder().key());

        this.tag(NEBlockTags.VIRAL_TATERS)
                .add(NEBlocks.VIRAL_TATER.builtInRegistryHolder().key());
    }
}
