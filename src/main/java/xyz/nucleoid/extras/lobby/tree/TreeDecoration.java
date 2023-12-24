package xyz.nucleoid.extras.lobby.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class TreeDecoration {
    public static final Codec<TreeDecoration> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Ornament.CODEC.listOf().fieldOf("ornaments").forGetter(t -> t.ornaments)
        ).apply(instance, TreeDecoration::new)
    );

    private final List<Ornament> ornaments;

    private TreeDecoration(List<Ornament> ornaments) {
        this.ornaments = ornaments;
    }

    public Collection<Ornament> getOrnaments() {
        return this.ornaments;
    }

    public TreeDecoration withOrnament(Ornament ornament) {
        var ornaments = new ArrayList<>(this.ornaments);

        // Remove existing ornaments with the same owner
        ornaments.removeIf(ornamentx -> {
            return ornamentx.owner().equals(ornament.owner());
        });

        ornaments.add(ornament);

        return new TreeDecoration(ornaments);
    }

    public TreeDecoration exceptOrnament(Ornament ornament) {
        var ornaments = new ArrayList<>(this.ornaments);

        ornaments.remove(ornament);

        return new TreeDecoration(ornaments);
    }

    public static TreeDecoration createEmpty() {
        return new TreeDecoration(List.of());
    }
}
