package xyz.nucleoid.extras.game_portal;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.api.registry.PlasmidRegistries;
import xyz.nucleoid.plasmid.impl.portal.GamePortalConfig;

public class ExtrasGamePortals {
    public static final boolean SHOW_INVALID = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    public static void register() {
        Registry.register(PlasmidRegistries.GAME_PORTAL_CONFIG, NucleoidExtras.identifier("styled/advanced_menu"), AdvancedStyledMenuPortalConfig.CODEC);
        Registry.register(PlasmidRegistries.GAME_PORTAL_CONFIG, NucleoidExtras.identifier("styled/handmade_menu"), HandmadeStyledMenuPortalConfig.CODEC);
        Registry.register(PlasmidRegistries.GAME_PORTAL_CONFIG, NucleoidExtras.identifier("styled/simple_menu"), SimpleStyledMenuPortalConfig.CODEC);
        Registry.register(PlasmidRegistries.GAME_PORTAL_CONFIG, NucleoidExtras.identifier("server_change"), ServerChangePortalConfig.CODEC);
    }
}
