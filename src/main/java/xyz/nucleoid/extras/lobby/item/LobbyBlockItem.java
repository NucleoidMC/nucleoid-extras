package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.item.VirtualBlockItem;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class LobbyBlockItem extends VirtualBlockItem {
    public LobbyBlockItem(Block block, Settings settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public void addTextToTooltip(List<Text> tooltip, ItemStack stack, ServerPlayerEntity player) {
        tooltip.add(new LiteralText("Do not use this block outside of lobby!").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false)));
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, ServerPlayerEntity player) {
        ItemStack out = super.getVirtualItemStack(itemStack, player);
        out.addEnchantment(Enchantments.INFINITY, 1);
        return out;
    }
}
