package xyz.nucleoid.extras.game_portal;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.GamePortalConfig;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuPortalConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record SimpleStyledMenuPortalConfig(
        Component name,
        Optional<Component> uiTitle,
        List<Component> description,
        ItemStack icon,
        List<MenuPortalConfig.Entry> games,
        CustomValuesConfig custom
) implements GamePortalConfig {

    public static final MapCodec<SimpleStyledMenuPortalConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                PlasmidCodecs.TEXT.optionalFieldOf("name", CommonComponents.EMPTY).forGetter(SimpleStyledMenuPortalConfig::name),
                PlasmidCodecs.TEXT.optionalFieldOf("ui_title").forGetter(SimpleStyledMenuPortalConfig::uiTitle),
                MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("description", Collections.emptyList()).forGetter(SimpleStyledMenuPortalConfig::description),
                MoreCodecs.ITEM_STACK.optionalFieldOf("icon", new ItemStack(Items.GRASS_BLOCK)).forGetter(SimpleStyledMenuPortalConfig::icon),
                MenuPortalConfig.Entry.CODEC.listOf().fieldOf("games").forGetter(config -> config.games),
                CustomValuesConfig.CODEC.optionalFieldOf("custom", CustomValuesConfig.empty()).forGetter(config -> config.custom)
        ).apply(instance, SimpleStyledMenuPortalConfig::new);
    });

    @Override
    public GamePortalBackend createBackend(MinecraftServer server, ResourceLocation id) {
        Component name;
        if (this.name != null && this.name != CommonComponents.EMPTY) {
            name = this.name;
        } else {
            name = Component.literal(id.toString());
        }

        return new SimpleStyledMenuPortalBackend(name, uiTitle.orElse(name), description, icon, this.games);
    }

    @Override
    public MapCodec<? extends GamePortalConfig> codec() {
        return CODEC;
    }
}
