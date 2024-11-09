package xyz.nucleoid.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.List;

public record RulesConfig(
    List<List<Text>> pages
) {
    private static final Codec<List<Text>> PAGE_CODEC = MoreCodecs.listOrUnit(PlasmidCodecs.TEXT);
    public static final Codec<RulesConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
        PAGE_CODEC.listOf().fieldOf("pages").forGetter(RulesConfig::pages)
    ).apply(i, RulesConfig::new));
}
