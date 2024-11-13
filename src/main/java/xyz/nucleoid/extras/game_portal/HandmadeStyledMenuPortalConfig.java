package xyz.nucleoid.extras.game_portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.GamePortalConfig;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntryConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record HandmadeStyledMenuPortalConfig(
        Text name,
        Optional<Text> uiTitle,
        List<Text> description,
        ItemStack icon,
        Map<Point, MenuEntryConfig> entries,
        CustomValuesConfig custom
) implements GamePortalConfig {

    public static final MapCodec<HandmadeStyledMenuPortalConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                PlasmidCodecs.TEXT.optionalFieldOf("name", ScreenTexts.EMPTY).forGetter(HandmadeStyledMenuPortalConfig::name),
                PlasmidCodecs.TEXT.optionalFieldOf("ui_title").forGetter(HandmadeStyledMenuPortalConfig::uiTitle),
                MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("description", Collections.emptyList()).forGetter(HandmadeStyledMenuPortalConfig::description),
                MoreCodecs.ITEM_STACK.optionalFieldOf("icon", new ItemStack(Items.GRASS_BLOCK)).forGetter(HandmadeStyledMenuPortalConfig::icon),
                Codec.unboundedMap(Point.CODEC, MenuEntryConfig.CODEC).fieldOf("entries").forGetter(HandmadeStyledMenuPortalConfig::entries),
                CustomValuesConfig.CODEC.optionalFieldOf("custom", CustomValuesConfig.empty()).forGetter(HandmadeStyledMenuPortalConfig::custom)
        ).apply(instance, HandmadeStyledMenuPortalConfig::new);
    });

    @Override
    public GamePortalBackend createBackend(MinecraftServer server, Identifier id) {
        Text name;
        if (this.name != null && this.name != ScreenTexts.EMPTY) {
            name = this.name;
        } else {
            name = Text.literal(id.toString());
        }

        return new HandmadeStyledMenuPortalBackend(name, uiTitle.orElse(name), description, icon, this.entries);
    }

    @Override
    public MapCodec<? extends GamePortalConfig> codec() {
        return CODEC;
    }


    public record Point(int x, int y) {
        public static final Codec<Point> CODEC = Codec.STRING.xmap(x -> {
            var split = x.split(":");
            return new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }, x -> x.x + ":" + x.y);
    }
}
