package xyz.nucleoid.extras.game_portal;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.game.portal.menu.InvalidMenuEntry;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntry;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntryConfig;

import java.util.ArrayList;
import java.util.List;

public final class AdvancedStyledMenuPortalBackend extends StyledMenuPortalBackend {
    private final List<MenuEntryConfig> configEntries;
    private List<MenuEntry> entries;

    public AdvancedStyledMenuPortalBackend(Text name, List<Text> description, ItemStack icon, List<MenuEntryConfig> config) {
        super(name, description, icon);
        this.configEntries = config;
    }

    @Override
    protected List<MenuEntry> getEntries() {
        if (this.entries == null) {
            this.entries = new ArrayList<>(this.configEntries.size());
            for (var config : configEntries) {
                var entry = config.createEntry();
                if (ExtrasGamePortals.SHOW_INVALID || !(entry instanceof InvalidMenuEntry)) {
                    this.entries.add(entry);
                }
            }
        }

        return this.entries;
    }
}
