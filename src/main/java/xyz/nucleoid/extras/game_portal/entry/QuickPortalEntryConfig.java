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

public record QuickPortalEntryConfig(
        Identifier portal,
        Identifier quickPortal,
        Optional<Text> name,
        Optional<List<Text>> description,
        Optional<ItemStack> icon
) implements MenuEntryConfig {
    public static final Codec<QuickPortalEntryConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("portal").forGetter(QuickPortalEntryConfig::portal),
            Identifier.CODEC.fieldOf("quick_portal").forGetter(QuickPortalEntryConfig::quickPortal),
            PlasmidCodecs.TEXT.optionalFieldOf("name").forGetter(QuickPortalEntryConfig::name),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("description").forGetter(QuickPortalEntryConfig::description),
            MoreCodecs.ITEM_STACK.optionalFieldOf("icon").forGetter(QuickPortalEntryConfig::icon)
    ).apply(instance, QuickPortalEntryConfig::new));

    @Override
    public MenuEntry createEntry() {
        var portal = GamePortalManager.INSTANCE.byId(this.portal);
        var quickPortal = GamePortalManager.INSTANCE.byId(this.quickPortal);

        if (portal != null && quickPortal != null) {
            return new QuickPortalEntry(
                    portal,
                    quickPortal,
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
