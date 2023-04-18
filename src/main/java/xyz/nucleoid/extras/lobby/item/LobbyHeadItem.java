package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyHeadItem extends PolymerHeadBlockItem {
    @SuppressWarnings("unchecked")
    public <T extends Block & PolymerHeadBlock> LobbyHeadItem(PolymerHeadBlock block, Settings settings) {
        super((T) block, settings);
    }

    @Override
    public Text getName() {
        return Text.empty().append(super.getName()).append(ScreenTexts.SPACE).append(Text.translatable("text.nucleoid_extras.lobby_only"));
    }

    @Override
    public Text getName(ItemStack stack) {
        return this.getName();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(false)));
    }
}
