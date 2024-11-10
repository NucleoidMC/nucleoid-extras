package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.RulesConfig;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

import java.util.ArrayList;
import java.util.List;

public class RuleBookItem extends Item implements PolymerItem {
    private static final CachedMapper<RulesConfig, List<RawFilteredPair<Text>>> ENCODED_PAGES = Util.cachedMapper(rules -> {
        List<RawFilteredPair<Text>> pages = new ArrayList<>();
        for (List<Text> page : rules.pages()) {
            Text combinedPage = Texts.join(page, Text.literal("\n"));
            pages.add(RawFilteredPair.of(combinedPage));
        }
        return pages;
    });

    public RuleBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
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
        LocalizationTarget localizationTarget = LocalizationTarget.forPacket();
        ServerLanguage targetLanguage = ServerTranslations.INSTANCE.getLanguage(localizationTarget);

        String translationKey = getTranslationKey();

        String title = targetLanguage.serverTranslations().get(translationKey);
        String author = targetLanguage.serverTranslations().get(translationKey + ".author");

        RulesConfig rules = NucleoidExtrasConfig.get().rules();

        WrittenBookContentComponent component = new WrittenBookContentComponent(
            RawFilteredPair.of(title),
            author,
            0,
            rules == null ? List.of() : ENCODED_PAGES.map(rules),
            true
        );

        ItemStack book = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);
        book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, component);

        //nbt.putInt("HideFlags", nbt.getInt("HideFlags") & ~ItemStack.TooltipSection.ADDITIONAL.getFlag());

        return book;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }
}
