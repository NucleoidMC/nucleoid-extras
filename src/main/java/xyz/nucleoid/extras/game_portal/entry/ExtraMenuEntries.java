package xyz.nucleoid.extras.game_portal.entry;

import net.minecraft.core.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.api.registry.PlasmidRegistries;
import xyz.nucleoid.plasmid.impl.portal.menu.MenuEntryConfig;

public class ExtraMenuEntries {
    public static void register() {
        Registry.register(PlasmidRegistries.MENU_ENTRY, NucleoidExtras.identifier("quick_portal"), QuickPortalEntryConfig.CODEC);
    }
}
