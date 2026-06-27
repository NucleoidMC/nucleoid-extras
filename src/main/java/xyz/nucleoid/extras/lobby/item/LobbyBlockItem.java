package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class LobbyBlockItem extends PolymerBlockItem {
    public LobbyBlockItem(Block block, Properties settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Component.translatable("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false)));
        if (this.getBlock() instanceof TooltipProvider appender) {
            appender.addToTooltip(context, textConsumer, type, stack);
        }
    }
}
