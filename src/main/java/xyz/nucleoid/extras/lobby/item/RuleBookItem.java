package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import xyz.nucleoid.extras.dialog.NEDialogs;
import xyz.nucleoid.packettweaker.PacketContext;

public class RuleBookItem extends Item implements PolymerItem {
    public RuleBookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer serverPlayer) {
            var dialog = world.registryAccess().get(NEDialogs.RULES);

            if (dialog.isPresent()) {
                serverPlayer.openDialog(dialog.get());

                user.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResult.SUCCESS_SERVER;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.WRITTEN_BOOK;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        String translationKey = getDescriptionId();

        ItemStack book = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        book.update(DataComponents.LORE, ItemLore.EMPTY, lore -> {
            return lore
                    .withLineAdded(formatLore(Component.translatable("book.byAuthor", Component.translatable(translationKey + ".author"))))
                    .withLineAdded(formatLore(Component.translatable("book.generation.0")));
        });

        book.update(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT, display -> {
            return display.withHidden(DataComponents.WRITTEN_BOOK_CONTENT, true);
        });

        return book;
    }

    @Override
    public ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    private static MutableComponent formatLore(MutableComponent text) {
        return text.withStyle(style -> {
            return style.withColor(ChatFormatting.GRAY).withItalic(false);
        });
    }
}
