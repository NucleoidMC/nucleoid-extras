package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyHeadItem extends BlockItem implements PolymerItem {
    private final String texture;

    @SuppressWarnings("unchecked")
    public LobbyHeadItem(Block block, Settings settings, String texture) {
        super(block, settings);
        this.texture = texture;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.PLAYER_HEAD;
    }

    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext tooltipContext, ServerPlayerEntity player) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, tooltipContext, player);
        if (this.texture != null) {
            out.getOrCreateNbt().put("SkullOwner", PolymerUtils.createSkullOwner(this.texture));
        }
        return out;
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
