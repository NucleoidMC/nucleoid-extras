package xyz.nucleoid.extras.game_portal;

import net.fabricmc.loader.api.FabricLoader;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.game.portal.GamePortalConfig;

public class ExtrasGamePortals {
    public static final boolean SHOW_INVALID = false && FabricLoader.getInstance().isDevelopmentEnvironment();

    public static void register() {
        GamePortalConfig.register(NucleoidExtras.identifier("styled/advanced_menu"), AdvancedStyledMenuPortalConfig.CODEC);
        GamePortalConfig.register(NucleoidExtras.identifier("styled/handmade_menu"), HandmadeStyledMenuPortalConfig.CODEC);
        GamePortalConfig.register(NucleoidExtras.identifier("styled/simple_menu"), SimpleStyledMenuPortalConfig.CODEC);
        GamePortalConfig.register(NucleoidExtras.identifier("server_change"), ServerChangePortalConfig.CODEC);
    }
}
