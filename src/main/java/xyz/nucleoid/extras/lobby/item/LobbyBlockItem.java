package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyBlockItem extends PolymerBlockItem {
    public LobbyBlockItem(Block block, Settings settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false)));
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        var out = super.getPolymerItemStack(itemStack, context, player);
        out.addEnchantment(Enchantments.INFINITY, 1);
        return out;
    }
}
