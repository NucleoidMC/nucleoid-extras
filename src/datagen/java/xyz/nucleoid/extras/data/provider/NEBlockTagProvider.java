package xyz.nucleoid.extras.data.provider;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.tag.NEBlockTags;

public class NEBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public NEBlockTagProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(dataOutput, registries);
    }

    @Override
    protected void configure(WrapperLookup lookup) {
        for (var block : TinyPotatoBlock.TATERS) {
            this.getOrCreateTagBuilder(NEBlockTags.COLLECTABLE_TATERS).add(block);
        }

        this.getOrCreateTagBuilder(BlockTags.DOORS)
                .add(NEBlocks.TRANSIENT_IRON_DOOR)
                .add(NEBlocks.TRANSIENT_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR);

        this.getOrCreateTagBuilder(NEBlockTags.LUCKY_TATER_DROPS)
                .add(NEBlocks.BRONZE_CAPSULE_TATER)
                .add(NEBlocks.SILVER_CAPSULE_TATER)
                .add(NEBlocks.GOLD_CAPSULE_TATER);

        this.getOrCreateTagBuilder(BlockTags.MOB_INTERACTABLE_DOORS)
                .add(NEBlocks.TRANSIENT_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR)
                .add(NEBlocks.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR);

        this.getOrCreateTagBuilder(NEBlockTags.NON_VIBRATING_TATERS)
                .addOptionalTag(BlockTags.DAMPENS_VIBRATIONS)
                .add(NEBlocks.WARDEN_TATER);

        this.getOrCreateTagBuilder(BlockTags.WOODEN_DOORS)
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

        this.getOrCreateTagBuilder(NEBlockTags.VIRAL_TATERS)
                .add(NEBlocks.VIRAL_TATER);
    }
}
