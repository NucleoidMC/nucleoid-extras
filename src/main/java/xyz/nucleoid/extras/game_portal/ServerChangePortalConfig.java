package xyz.nucleoid.extras.game_portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.game.config.CustomValuesConfig;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.GamePortalConfig;

import java.util.Collections;
import java.util.List;

public record ServerChangePortalConfig(
        Component name,
        List<Component> description,
        ItemStackTemplate icon,
        String serverId,
        CustomValuesConfig custom
) implements GamePortalConfig {

    public static final MapCodec<ServerChangePortalConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(
                PlasmidCodecs.TEXT.optionalFieldOf("name", CommonComponents.EMPTY).forGetter(ServerChangePortalConfig::name),
                MoreCodecs.listOrUnit(PlasmidCodecs.TEXT).optionalFieldOf("description", Collections.emptyList()).forGetter(ServerChangePortalConfig::description),
                ItemStackTemplate.CODEC.optionalFieldOf("icon", new ItemStackTemplate(Items.GRASS_BLOCK)).forGetter(ServerChangePortalConfig::icon),
                Codec.STRING.fieldOf("server_id").forGetter(ServerChangePortalConfig::serverId),
                CustomValuesConfig.CODEC.optionalFieldOf("custom", CustomValuesConfig.empty()).forGetter(config -> config.custom)
        ).apply(instance, ServerChangePortalConfig::new);
    });

    @Override
    public GamePortalBackend createBackend(MinecraftServer server, Identifier id) {
        Component name;
        if (this.name != null && this.name != CommonComponents.EMPTY) {
            name = this.name;
        } else {
            name = Component.literal(id.toString());
        }

        return new ServerChangePortalBackend(name, description, icon.create(), this.serverId);
    }

    @Override
    public MapCodec<? extends GamePortalConfig> codec() {
        return CODEC;
    }
}
