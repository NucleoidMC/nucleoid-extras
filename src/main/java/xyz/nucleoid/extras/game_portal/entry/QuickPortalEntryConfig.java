package xyz.nucleoid.extras.game_portal.entry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;
import xyz.nucleoid.plasmid.impl.portal.GamePortalManager;
import xyz.nucleoid.plasmid.impl.portal.menu.*;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record QuickPortalEntryConfig(
        ResourceLocation portal,
        ResourceLocation quickPortal,
        Component message,
        Optional<Component> name,
        Optional<List<Component>> description,
        Optional<ItemStack> icon
) implements MenuEntryConfig {
    public static final MapCodec<QuickPortalEntryConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("portal").forGetter(QuickPortalEntryConfig::portal),
            ResourceLocation.CODEC.fieldOf("quick_portal").forGetter(QuickPortalEntryConfig::quickPortal),
            PlasmidCodecs.TEXT.fieldOf("message").orElse(Component.translatable("text.nucleoid_extras.ui.action.more")).forGetter(QuickPortalEntryConfig::message),
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
                    this.message,
                    this.name.orElse(portal.getName()),
                    this.description.orElse(portal.getDescription()),
                    this.icon.orElse(portal.getIcon())
            );
        }

        return new InvalidMenuEntry(this.name);
    }

    @Override
    public MapCodec<? extends MenuEntryConfig> codec() {
        return CODEC;
    }
}
