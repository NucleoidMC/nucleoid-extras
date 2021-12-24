package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.block.VirtualHeadBlock;
import eu.pb4.polymer.item.VirtualHeadBlockItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyHeadItem extends VirtualHeadBlockItem {
    public LobbyHeadItem(VirtualHeadBlock block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName() {
        return new LiteralText("").append(super.getName()).append(" ").append(new TranslatableText("text.nucleoid_extras.lobby_only"));
    }

    @Override
    public Text getName(ItemStack stack) {
        return this.getName();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false)));
    }
}
