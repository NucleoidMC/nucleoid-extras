package xyz.nucleoid.extras.game_portal;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableInt;
import xyz.nucleoid.plasmid.game.portal.menu.InvalidMenuEntry;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntry;
import xyz.nucleoid.plasmid.game.portal.menu.MenuEntryConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandmadeStyledMenuPortalBackend extends StyledMenuPortalBackend {
    private final Map<HandmadeStyledMenuPortalConfig.Point, MenuEntryConfig> configEntries;
    private List<MenuEntry> entries;
    private List<Entry> guiEntries;

    public HandmadeStyledMenuPortalBackend(Text name, List<Text> description, ItemStack icon, Map<HandmadeStyledMenuPortalConfig.Point, MenuEntryConfig> config) {
        super(name, description, icon);
        this.configEntries = config;
    }

    @Override
    protected List<MenuEntry> getEntries() {
        if (this.entries == null) {
            this.entries = new ArrayList<>(this.getGuiEntries().size());
            for (var config : this.getGuiEntries()) {
                this.entries.add(config.entry);
            }
        }

        return this.entries;
    }

    @Override
    protected void fillInterface(ServerPlayerEntity player, SimpleGui gui, MutableInt page) {
        for (var entry : this.getGuiEntries()) {
            gui.setSlot(entry.pos, createIconFor(entry.entry));
        }
    }

    private List<Entry> getGuiEntries() {
        if (this.guiEntries == null) {
            List<Entry> list = new ArrayList<>();
            for (Map.Entry<HandmadeStyledMenuPortalConfig.Point, MenuEntryConfig> x : this.configEntries.entrySet()) {
                var a = x.getValue().createEntry();
                if (ExtrasGamePortals.SHOW_INVALID || !(a instanceof InvalidMenuEntry)) {
                    var entry = new Entry(x.getKey().x() + x.getKey().y() * 9, a);
                    list.add(entry);
                }
            }
            this.guiEntries = list;
        }

        return this.guiEntries;
    }

    record Entry(int pos, MenuEntry entry){}
}
