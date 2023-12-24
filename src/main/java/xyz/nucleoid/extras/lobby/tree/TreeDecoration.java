package xyz.nucleoid.extras.lobby.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public final class TreeDecoration {
    public static final Codec<TreeDecoration> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Ornament.CODEC.listOf().fieldOf("ornaments").forGetter(t -> t.ornaments),
                TagKey.codec(RegistryKeys.BLOCK).fieldOf("supported_blocks").forGetter(TreeDecoration::getSupportedBlocks)
        ).apply(instance, TreeDecoration::new)
    );

    private final List<Ornament> ornaments;
    private final TagKey<Block> supportedBlocks;

    private TreeDecoration(List<Ornament> ornaments, TagKey<Block> supportedBlocks) {
        this.ornaments = ornaments;
        this.supportedBlocks = supportedBlocks;
    }

    public Collection<Ornament> getOrnaments() {
        return this.ornaments;
    }

    public TagKey<Block> getSupportedBlocks() {
        return this.supportedBlocks;
    }

    public TreeDecoration withOrnament(Ornament ornament) {
        var ornaments = new ArrayList<>(this.ornaments);

        // Remove existing ornaments with the same owner
        ornaments.removeIf(ornamentx -> {
            return ornamentx.owner().equals(ornament.owner());
        });

        ornaments.add(ornament);

        return new TreeDecoration(ornaments, this.supportedBlocks);
    }

    public TreeDecoration exceptOrnament(Ornament ornament) {
        var ornaments = new ArrayList<>(this.ornaments);

        ornaments.remove(ornament);

        return new TreeDecoration(ornaments, this.supportedBlocks);
    }

    public static TreeDecoration createEmpty() {
        return new TreeDecoration(List.of(), BlockTags.LEAVES);
    }
}
