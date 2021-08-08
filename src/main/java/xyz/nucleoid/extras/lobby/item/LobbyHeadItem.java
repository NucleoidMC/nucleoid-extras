package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.block.VirtualHeadBlock;
import eu.pb4.polymer.item.VirtualHeadBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

public class LobbyHeadItem extends VirtualHeadBlockItem {
    public LobbyHeadItem(VirtualHeadBlock block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void modifyTooltip(List<Text> tooltip, ItemStack stack, ServerPlayerEntity player) {
        tooltip.add(new TranslatableText("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false)));
    }
}
