package xyz.nucleoid.extras.tag;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import xyz.nucleoid.extras.NucleoidExtras;

public final class NEBlockTags {
    public static final TagKey<Block> COLLECTABLE_TATERS = of("collectable_taters");
    public static final TagKey<Block> LUCKY_TATER_DROPS = of("lucky_tater_drops");
    public static final TagKey<Block> NON_VIBRATING_TATERS = of("non_vibrating_taters");
    public static final TagKey<Block> VIRAL_TATERS = of("viral_taters");

    private NEBlockTags() {
        return;
    }

    private static TagKey<Block> of(String path) {
        return TagKey.of(RegistryKeys.BLOCK, NucleoidExtras.identifier(path));
    }
}
