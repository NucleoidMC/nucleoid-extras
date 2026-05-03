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
            this.valueLookupBuilder(NEBlockTags.COLLECTABLE_TATERS).add(block);
        }

        this.valueLookupBuilder(BlockTags.DOORS)
                .add(NEBlocks.TRANSIENT_IRON_DOOR)
                .add(NEBlocks.TRANSIENT_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR);

        this.valueLookupBuilder(NEBlockTags.LUCKY_TATER_DROPS)
                .add(NEBlocks.BRONZE_CAPSULE_TATER)
                .add(NEBlocks.SILVER_CAPSULE_TATER)
                .add(NEBlocks.GOLD_CAPSULE_TATER);

        this.valueLookupBuilder(BlockTags.MOB_INTERACTABLE_DOORS)
                .add(NEBlocks.TRANSIENT_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR);

        this.valueLookupBuilder(NEBlockTags.NON_VIBRATING_TATERS)
                .addOptionalTag(BlockTags.DAMPENS_VIBRATIONS)
                .add(NEBlocks.WARDEN_TATER);

        this.valueLookupBuilder(BlockTags.WOODEN_DOORS)
                .add(NEBlocks.TRANSIENT_OAK_DOOR)
                .add(NEBlocks.TRANSIENT_SPRUCE_DOOR)
                .add(NEBlocks.TRANSIENT_BIRCH_DOOR)
                .add(NEBlocks.TRANSIENT_JUNGLE_DOOR)
                .add(NEBlocks.TRANSIENT_ACACIA_DOOR)
                .add(NEBlocks.TRANSIENT_CHERRY_DOOR)
                .add(NEBlocks.TRANSIENT_DARK_OAK_DOOR)
                .add(NEBlocks.TRANSIENT_MANGROVE_DOOR)
                .add(NEBlocks.TRANSIENT_PALE_OAK_DOOR)
                .add(NEBlocks.TRANSIENT_BAMBOO_DOOR)
                .add(NEBlocks.TRANSIENT_CRIMSON_DOOR)
                .add(NEBlocks.TRANSIENT_WARPED_DOOR);

        this.valueLookupBuilder(NEBlockTags.VIRAL_TATERS)
                .add(NEBlocks.VIRAL_TATER);
    }
}
