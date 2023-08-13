package xyz.nucleoid.extras.game_portal.entry;

import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntryConfig;

public class ExtraMenuEntries {
    public static void register() {
        MenuEntryConfig.register(NucleoidExtras.identifier("detailed_game"), DetailedGameEntryConfig.CODEC);
    }
}
