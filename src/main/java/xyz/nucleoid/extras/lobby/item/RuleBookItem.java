package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.RulesConfig;
import xyz.nucleoid.server.translations.api.LocalizationTarget;
import xyz.nucleoid.server.translations.api.language.ServerLanguage;
import xyz.nucleoid.server.translations.impl.ServerTranslations;

import java.util.List;

public class RuleBookItem extends Item implements PolymerItem {
    private static final CachedMapper<RulesConfig, NbtList> ENCODED_PAGES = Util.cachedMapper(rules -> {
        NbtList pages = new NbtList();
        for (List<Text> page : rules.pages()) {
            Text combinedPage = Texts.join(page, Text.literal("\n"));
            pages.add(NbtString.of(Text.Serialization.toJsonString(combinedPage)));
        }
        return pages;
    });

    public RuleBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.WRITTEN_BOOK;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        LocalizationTarget localizationTarget = player != null ? LocalizationTarget.of(player) : LocalizationTarget.ofSystem();
        ServerLanguage targetLanguage = ServerTranslations.INSTANCE.getLanguage(localizationTarget);

        String translationKey = getTranslationKey();
        ItemStack book = PolymerItem.super.getPolymerItemStack(itemStack, context, player);
        NbtCompound nbt = book.getOrCreateNbt();
        nbt.putInt("HideFlags", nbt.getInt("HideFlags") & ~ItemStack.TooltipSection.ADDITIONAL.getFlag());
        nbt.putString("title", targetLanguage.serverTranslations().get(translationKey));
        nbt.putString("author", targetLanguage.serverTranslations().get(translationKey + ".author"));

        RulesConfig rules = NucleoidExtrasConfig.get().rules();
        if (rules != null) {
            nbt.put("pages", ENCODED_PAGES.map(rules));
        }

        return book;
    }
}
