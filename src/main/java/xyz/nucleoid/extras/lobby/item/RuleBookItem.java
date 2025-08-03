package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.RulesConfig;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuleBookItem extends Item implements PolymerItem {
    private static final CachedMapper<RulesConfig, List<RawFilteredPair<Text>>> ENCODED_PAGES = Util.cachedMapper(rules -> {
        List<RawFilteredPair<Text>> pages = new ArrayList<>();
        for (List<Text> page : rules.pages()) {
            Text combinedPage = Texts.join(page, Text.literal("\n"));
            pages.add(RawFilteredPair.of(combinedPage));
        }
        return pages;
    });

    private static final int DIALOG_BODY_WIDTH = 300;

    public RuleBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            var body = new ArrayList<DialogBody>();
            RulesConfig rules = NucleoidExtrasConfig.get().rules();

            if (rules != null) {
                var pages = ENCODED_PAGES.map(rules);

                for (var page : pages) {
                    body.add(new PlainMessageDialogBody(page.raw(), DIALOG_BODY_WIDTH));
                }
            }

            var dialog = new NoticeDialog(new DialogCommonData(getName(), Optional.empty(), true, false, AfterAction.CLOSE, body, List.of()), NoticeDialog.OK_BUTTON);
            serverPlayer.openDialog(RegistryEntry.of(dialog));
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return ActionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.WRITTEN_BOOK;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        String translationKey = getTranslationKey();

        ItemStack book = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        book.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, lore -> {
            return lore
                    .with(formatLore(Text.translatable("book.byAuthor", Text.translatable(translationKey + ".author"))))
                    .with(formatLore(Text.translatable("book.generation.0")));
        });

        book.apply(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT, display -> {
            return display.with(DataComponentTypes.WRITTEN_BOOK_CONTENT, true);
        });

        return book;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    private static MutableText formatLore(MutableText text) {
        return text.styled(style -> {
            return style.withColor(Formatting.GRAY).withItalic(false);
        });
    }
}
