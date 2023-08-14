package xyz.nucleoid.extras.game_portal.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.game.portal.GamePortalManager;
import xyz.nucleoid.plasmid.game.portal.menu.*;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.List;
import java.util.Optional;

public record OptionPortalEntryConfig(
        Identifier portal,
        Identifier detailPortal,
        Optional<Text> name,
        Optional<List<Text>> description,
        Optional<ItemStack> icon
) implements MenuEntryConfig {
    public static final Codec<OptionPortalEntryConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("portal").forGetter(OptionPortalEntryConfig::portal),
            Identifier.CODEC.fieldOf("detail_portal").forGetter(OptionPortalEntryConfig::detailPortal),
            PlasmidCodecs.TEXT.optionalFieldOf("name").forGetter(OptionPortalEntryConfig::name),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("description").forGetter(OptionPortalEntryConfig::description),
            MoreCodecs.ITEM_STACK.optionalFieldOf("icon").forGetter(OptionPortalEntryConfig::icon)
    ).apply(instance, OptionPortalEntryConfig::new));

    @Override
    public MenuEntry createEntry() {
        var portal = GamePortalManager.INSTANCE.byId(this.detailPortal);
        var detailPortal = GamePortalManager.INSTANCE.byId(this.detailPortal);

        if (portal != null && detailPortal != null) {
            return new OptionPortalEntry(
                    portal,
                    detailPortal,
                    this.name.orElse(portal.getName()),
                    this.description.orElse(portal.getDescription()),
                    this.icon.orElse(portal.getIcon())
            );
        }

        return new InvalidMenuEntry(this.name);
    }

    @Override
    public Codec<? extends MenuEntryConfig> codec() {
        return CODEC;
    }
}
