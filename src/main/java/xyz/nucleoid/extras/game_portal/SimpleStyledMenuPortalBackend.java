package xyz.nucleoid.extras.game_portal;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;
import xyz.nucleoid.extras.util.CommonGuiElements;
import xyz.nucleoid.extras.util.PagedGui;
import xyz.nucleoid.plasmid.game.config.GameConfigs;
import xyz.nucleoid.plasmid.game.portal.game.ConcurrentGamePortalBackend;
import xyz.nucleoid.plasmid.game.portal.menu.*;

import java.util.ArrayList;
import java.util.List;

public final class SimpleStyledMenuPortalBackend extends StyledMenuPortalBackend {
    private final List<MenuPortalConfig.Entry> configEntries;
    private List<MenuEntry> entries;

    public SimpleStyledMenuPortalBackend(Text name, List<Text> description, ItemStack icon, List<MenuPortalConfig.Entry> config) {
        super(name, description, icon);
        this.configEntries = config;
    }

    @Override
    protected List<MenuEntry> getEntries() {
        if (this.entries == null) {
            this.entries = new ArrayList<>(this.configEntries.size());
            for (var configEntry : configEntries) {
                var game = new ConcurrentGamePortalBackend(configEntry.game());
                var gameConfig = GameConfigs.get(configEntry.game());

                if (gameConfig != null) {
                    this.entries.add(new GameMenuEntry(
                        game,
                        configEntry.name().orElse(gameConfig.name()),
                        configEntry.description().orElse(gameConfig.description()),
                        configEntry.icon().orElse(gameConfig.icon())
                    ));
                } else if (ExtrasGamePortals.SHOW_INVALID) {
                    this.entries.add(new InvalidMenuEntry(game.getName()));
                }
            }
        }

        return this.entries;
    }
}
