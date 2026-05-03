package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class LobbyHeadItem extends BlockItem implements PolymerItem {
    private final String texture;

    @SuppressWarnings("unchecked")
    public LobbyHeadItem(Block block, Properties settings, String texture) {
        super(block, settings);
        this.texture = texture;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PLAYER_HEAD;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context, HolderLookup.Provider provider) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context, provider);
        if (this.texture != null) {
            out.set(DataComponents.PROFILE, PolymerUtils.createProfileComponent(this.texture));
        }
        return out;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.empty().append(super.getName(stack)).append(CommonComponents.SPACE).append(Component.translatable("text.nucleoid_extras.lobby_only"));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Component.translatable("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false)));
    }
}
