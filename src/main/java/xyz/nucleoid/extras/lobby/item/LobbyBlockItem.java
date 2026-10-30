package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import xyz.nucleoid.extras.component.NEDataComponentTypes;

import java.util.function.Consumer;

public class LobbyBlockItem extends PolymerBlockItem {
    public LobbyBlockItem(Block block, Properties settings, Item virtualItem) {
        super(block, settings.component(NEDataComponentTypes.LOBBY_WARNING, Unit.INSTANCE), virtualItem);
    }
}
