package xyz.nucleoid.extras.game_portal.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.game.config.GameConfigs;
import xyz.nucleoid.plasmid.game.portal.GamePortalManager;
import xyz.nucleoid.plasmid.game.portal.game.ConcurrentGamePortalBackend;
import xyz.nucleoid.plasmid.game.portal.menu.*;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.List;
import java.util.Optional;

public record DetailedGameEntryConfig(
        Identifier game,
        Identifier detailPortal,
        Optional<Text> name,
        Optional<List<Text>> description,
        Optional<ItemStack> icon
) implements MenuEntryConfig {
    public static final Codec<DetailedGameEntryConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("game").forGetter(DetailedGameEntryConfig::game),
            Identifier.CODEC.fieldOf("detail_portal").forGetter(DetailedGameEntryConfig::detailPortal),
            PlasmidCodecs.TEXT.optionalFieldOf("name").forGetter(DetailedGameEntryConfig::name),
            MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("description").forGetter(DetailedGameEntryConfig::description),
            MoreCodecs.ITEM_STACK.optionalFieldOf("icon").forGetter(DetailedGameEntryConfig::icon)
    ).apply(instance, DetailedGameEntryConfig::new));

    @Override
    public MenuEntry createEntry() {
        var game = new ConcurrentGamePortalBackend(this.game);
        var gameConfig = GameConfigs.get(this.game);
        var detailPortal = GamePortalManager.INSTANCE.byId(this.detailPortal);

        if (gameConfig == null) {
            return new InvalidMenuEntry(game.getName());
        }
        else if (detailPortal != null) {
            return new DetailedGameEntry(
                    game,
                    detailPortal,
                    this.name.orElse(gameConfig.name()),
                    this.description.orElse(gameConfig.description()),
                    this.icon.orElse(gameConfig.icon())
            );
        }

        return new InvalidMenuEntry(this.name);
    }

    @Override
    public Codec<? extends MenuEntryConfig> codec() {
        return CODEC;
    }
}
