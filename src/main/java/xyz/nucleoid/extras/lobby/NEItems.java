package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.api.block.PolymerHeadBlock;
import eu.pb4.polymer.api.item.PolymerItemGroup;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.item.LobbyBlockItem;
import xyz.nucleoid.extras.lobby.item.LobbyHeadItem;
import xyz.nucleoid.extras.lobby.item.QuickArmorStandItem;
import xyz.nucleoid.extras.lobby.item.TaterBoxItem;

import java.util.Collections;

public class NEItems {
    public static final PolymerItemGroup ITEM_GROUP = PolymerItemGroup.create(NucleoidExtras.identifier("general"), new TranslatableText("text.nucleoid_extras.name"));

    public static final Item NUCLEOID_LOGO = createHead(NEBlocks.NUCLEOID_LOGO);

    public static final Item END_PORTAL = createSimple(NEBlocks.END_PORTAL, Items.BLACK_CARPET);
    public static final Item END_GATEWAY = createSimple(NEBlocks.END_GATEWAY, Items.BLACK_WOOL);
    public static final Item SAFE_TNT = createSimple(NEBlocks.SAFE_TNT, Items.TNT);

    public static final Item GOLD_LAUNCH_PAD = createSimple(NEBlocks.GOLD_LAUNCH_PAD, Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Item IRON_LAUNCH_PAD = createSimple(NEBlocks.IRON_LAUNCH_PAD, Items.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final Item INFINITE_DISPENSER = createSimple(NEBlocks.INFINITE_DISPENSER, Items.DISPENSER);
    public static final Item INFINITE_DROPPER = createSimple(NEBlocks.INFINITE_DROPPER, Items.DROPPER);

    public static final Item BLACK_CONCRETE_POWDER = createSimple(NEBlocks.BLACK_CONCRETE_POWDER, Items.BLACK_CONCRETE_POWDER);
    public static final Item BLUE_CONCRETE_POWDER = createSimple(NEBlocks.BLUE_CONCRETE_POWDER, Items.BLUE_CONCRETE_POWDER);
    public static final Item BROWN_CONCRETE_POWDER = createSimple(NEBlocks.BROWN_CONCRETE_POWDER, Items.BROWN_CONCRETE_POWDER);
    public static final Item CYAN_CONCRETE_POWDER = createSimple(NEBlocks.CYAN_CONCRETE_POWDER, Items.CYAN_CONCRETE_POWDER);
    public static final Item GREEN_CONCRETE_POWDER = createSimple(NEBlocks.GREEN_CONCRETE_POWDER, Items.GREEN_CONCRETE_POWDER);
    public static final Item GRAY_CONCRETE_POWDER = createSimple(NEBlocks.GRAY_CONCRETE_POWDER, Items.GRAY_CONCRETE_POWDER);
    public static final Item LIGHT_BLUE_CONCRETE_POWDER = createSimple(NEBlocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Item LIGHT_GRAY_CONCRETE_POWDER = createSimple(NEBlocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Item LIME_CONCRETE_POWDER = createSimple(NEBlocks.LIME_CONCRETE_POWDER, Items.LIME_CONCRETE_POWDER);
    public static final Item MAGENTA_CONCRETE_POWDER = createSimple(NEBlocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_CONCRETE_POWDER);
    public static final Item ORANGE_CONCRETE_POWDER = createSimple(NEBlocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_CONCRETE_POWDER);
    public static final Item PINK_CONCRETE_POWDER = createSimple(NEBlocks.PINK_CONCRETE_POWDER, Items.PINK_CONCRETE_POWDER);
    public static final Item PURPLE_CONCRETE_POWDER = createSimple(NEBlocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_CONCRETE_POWDER);
    public static final Item RED_CONCRETE_POWDER = createSimple(NEBlocks.RED_CONCRETE_POWDER, Items.RED_CONCRETE_POWDER);
    public static final Item WHITE_CONCRETE_POWDER = createSimple(NEBlocks.WHITE_CONCRETE_POWDER, Items.WHITE_CONCRETE_POWDER);
    public static final Item YELLOW_CONCRETE_POWDER = createSimple(NEBlocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_CONCRETE_POWDER);

    public static final Item TINY_POTATO = createHead(NEBlocks.TINY_POTATO);
    public static final Item IRRITATER = createHead(NEBlocks.IRRITATER);
    public static final Item SAD_TATER = createHead(NEBlocks.SAD_TATER);
    public static final Item AZALEA_TATER = createHead(NEBlocks.AZALEA_TATER);
    public static final Item STONE_TATER = createHead(NEBlocks.STONE_TATER);
    public static final Item CALCITE_TATER = createHead(NEBlocks.CALCITE_TATER);
    public static final Item TUFF_TATER = createHead(NEBlocks.TUFF_TATER);
    public static final Item DRIPSTONE_TATER = createHead(NEBlocks.DRIPSTONE_TATER);
    public static final Item AMETHYST_TATER = createHead(NEBlocks.AMETHYST_TATER);
    public static final Item PACKED_ICE_TATER = createHead(NEBlocks.PACKED_ICE_TATER);
    public static final Item BLUE_ICE_TATER = createHead(NEBlocks.BLUE_ICE_TATER);
    public static final Item FLAME_TATER = createHead(NEBlocks.FLAME_TATER);
    public static final Item PUZZLE_CUBE_TATER = createHead(NEBlocks.PUZZLE_CUBE_TATER);
    public static final Item CRATE_TATER = createHead(NEBlocks.CRATE_TATER);
    public static final Item TATER_OF_UNDYING = createHead(NEBlocks.TATER_OF_UNDYING);
    public static final Item CRYING_OBSIDIAN_TATER = createHead(NEBlocks.CRYING_OBSIDIAN_TATER);
    public static final Item DICE_TATER = createHead(NEBlocks.DICE_TATER);
    public static final Item TRANS_TATER = createHead(NEBlocks.TRANS_TATER);
    public static final Item ASEXUAL_TATER = createHead(NEBlocks.ASEXUAL_TATER);
    public static final Item BI_TATER = createHead(NEBlocks.BI_TATER);
    public static final Item GAY_TATER = createHead(NEBlocks.GAY_TATER);
    public static final Item LESBIAN_TATER = createHead(NEBlocks.LESBIAN_TATER);
    public static final Item NONBINARY_TATER = createHead(NEBlocks.NONBINARY_TATER);
    public static final Item PAN_TATER = createHead(NEBlocks.PAN_TATER);
    public static final Item WARDEN_TATER = createHead(NEBlocks.WARDEN_TATER);
    public static final Item TATEROID = createHead(NEBlocks.TATEROID);
    public static final Item FLIPPED_TATER = createHead(NEBlocks.FLIPPED_TATER);
    public static final Item SANTA_HAT_TATER = createHead(NEBlocks.SANTA_HAT_TATER);
    public static final Item GENDERFLUID_TATER = createHead(NEBlocks.GENDERFLUID_TATER);
    public static final Item DEMISEXUAL_TATER = createHead(NEBlocks.DEMISEXUAL_TATER);

    public static final Item SKELETATER = createHead(NEBlocks.SKELETATER);
    public static final Item WITHER_SKELETATER = createHead(NEBlocks.WITHER_SKELETATER);
    public static final Item ZOMBIE_TATER = createHead(NEBlocks.ZOMBIE_TATER);
    public static final Item CREEPER_TATER = createHead(NEBlocks.CREEPER_TATER);
    public static final Item STEVE_TATER = createHead(NEBlocks.STEVE_TATER);
    public static final Item ALEX_TATER = createHead(NEBlocks.ALEX_TATER);

    public static final Item WHITE_TATER = createHead(NEBlocks.WHITE_TATER);
    public static final Item ORANGE_TATER = createHead(NEBlocks.ORANGE_TATER);
    public static final Item MAGENTA_TATER = createHead(NEBlocks.MAGENTA_TATER);
    public static final Item LIGHT_BLUE_TATER = createHead(NEBlocks.LIGHT_BLUE_TATER);
    public static final Item YELLOW_TATER = createHead(NEBlocks.YELLOW_TATER);
    public static final Item LIME_TATER = createHead(NEBlocks.LIME_TATER);
    public static final Item PINK_TATER = createHead(NEBlocks.PINK_TATER);
    public static final Item GRAY_TATER = createHead(NEBlocks.GRAY_TATER);
    public static final Item LIGHT_GRAY_TATER = createHead(NEBlocks.LIGHT_GRAY_TATER);
    public static final Item CYAN_TATER = createHead(NEBlocks.CYAN_TATER);
    public static final Item PURPLE_TATER = createHead(NEBlocks.PURPLE_TATER);
    public static final Item BLUE_TATER = createHead(NEBlocks.BLUE_TATER);
    public static final Item BROWN_TATER = createHead(NEBlocks.BROWN_TATER);
    public static final Item GREEN_TATER = createHead(NEBlocks.GREEN_TATER);
    public static final Item RED_TATER = createHead(NEBlocks.RED_TATER);
    public static final Item BLACK_TATER = createHead(NEBlocks.BLACK_TATER);

    public static final Item TATER_BOX = new TaterBoxItem(new Item.Settings().group(ITEM_GROUP).maxDamage(0));
    public static final Item QUICK_ARMOR_STAND = new QuickArmorStandItem(new Item.Settings().group(ITEM_GROUP));

    private static Item createHead(Block head) {
        return new LobbyHeadItem((PolymerHeadBlock) head, new Item.Settings().group(ITEM_GROUP));
    }

    private static Item createSimple(Block block, Item virtual) {
        return new LobbyBlockItem(block, new Item.Settings().group(ITEM_GROUP), virtual);
    }

    public static void register() {
        register("nucleoid_logo", NUCLEOID_LOGO);
        register("end_portal", END_PORTAL);
        register("end_gateway", END_GATEWAY);
        register("safe_tnt", SAFE_TNT);
        register("gold_launch_pad", GOLD_LAUNCH_PAD);
        register("iron_launch_pad", IRON_LAUNCH_PAD);
        register("infinite_dispenser", INFINITE_DISPENSER);
        register("infinite_dropper", INFINITE_DROPPER);

        register("black_concrete_powder", BLACK_CONCRETE_POWDER);
        register("blue_concrete_powder", BLUE_CONCRETE_POWDER);
        register("brown_concrete_powder", BROWN_CONCRETE_POWDER);
        register("cyan_concrete_powder", CYAN_CONCRETE_POWDER);
        register("green_concrete_powder", GREEN_CONCRETE_POWDER);
        register("gray_concrete_powder", GRAY_CONCRETE_POWDER);
        register("light_blue_concrete_powder", LIGHT_BLUE_CONCRETE_POWDER);
        register("light_gray_concrete_powder", LIGHT_GRAY_CONCRETE_POWDER);
        register("lime_concrete_powder", LIME_CONCRETE_POWDER);
        register("magenta_concrete_powder", MAGENTA_CONCRETE_POWDER);
        register("orange_concrete_powder", ORANGE_CONCRETE_POWDER);
        register("pink_concrete_powder", PINK_CONCRETE_POWDER);
        register("purple_concrete_powder", PURPLE_CONCRETE_POWDER);
        register("red_concrete_powder", RED_CONCRETE_POWDER);
        register("white_concrete_powder", WHITE_CONCRETE_POWDER);
        register("yellow_concrete_powder", YELLOW_CONCRETE_POWDER);

        register("tiny_potato", TINY_POTATO);
        register("irritater", IRRITATER);
        register("sad_tater", SAD_TATER);
        register("azalea_tater", AZALEA_TATER);
        register("stone_tater", STONE_TATER);
        register("calcite_tater", CALCITE_TATER);
        register("tuff_tater", TUFF_TATER);
        register("dripstone_tater", DRIPSTONE_TATER);
        register("amethyst_tater", AMETHYST_TATER);
        register("packed_ice_tater", PACKED_ICE_TATER);
        register("blue_ice_tater", BLUE_ICE_TATER);
        register("flame_tater", FLAME_TATER);

        register("puzzle_cube_tater", PUZZLE_CUBE_TATER);
        register("crate_tater", CRATE_TATER);
        register("tater_of_undying", TATER_OF_UNDYING);
        register("crying_obsidian_tater", CRYING_OBSIDIAN_TATER);

        register("dice_tater", DICE_TATER);
        register("trans_tater", TRANS_TATER);
        register("asexual_tater", ASEXUAL_TATER);
        register("bi_tater", BI_TATER);
        register("gay_tater", GAY_TATER);
        register("lesbian_tater", LESBIAN_TATER);
        register("nonbinary_tater", NONBINARY_TATER);
        register("pan_tater", PAN_TATER);
        register("warden_tater", WARDEN_TATER);
        register("tateroid", TATEROID);
        register("flipped_tater", FLIPPED_TATER);
        register("santa_hat_tater", SANTA_HAT_TATER);
        register("genderfluid_tater", GENDERFLUID_TATER);
        register("demisexual_tater", DEMISEXUAL_TATER);

        register("skeletater", SKELETATER);
        register("wither_skeletater", WITHER_SKELETATER);
        register("zombie_tater", ZOMBIE_TATER);
        register("creeper_tater", CREEPER_TATER);
        register("steve_tater", STEVE_TATER);
        register("alex_tater", ALEX_TATER);

        register("white_tater", WHITE_TATER);
        register("orange_tater", ORANGE_TATER);
        register("magenta_tater", MAGENTA_TATER);
        register("light_blue_tater", LIGHT_BLUE_TATER);
        register("yellow_tater", YELLOW_TATER);
        register("lime_tater", LIME_TATER);
        register("pink_tater", PINK_TATER);
        register("gray_tater", GRAY_TATER);
        register("light_gray_tater", LIGHT_GRAY_TATER);
        register("cyan_tater", CYAN_TATER);
        register("purple_tater", PURPLE_TATER);
        register("blue_tater", BLUE_TATER);
        register("brown_tater", BROWN_TATER);
        register("green_tater", GREEN_TATER);
        register("red_tater", RED_TATER);
        register("black_tater", BLACK_TATER);

        register("tater_box", TATER_BOX);
        register("quick_armor_stand", QUICK_ARMOR_STAND);

        ServerPlayConnectionEvents.JOIN.register(NEItems::onPlayerJoin);

        ITEM_GROUP.setIcon(NUCLEOID_LOGO.getDefaultStack());
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer server) {
        if (!handler.getPlayer().getInventory().containsAny(Collections.singleton(TATER_BOX))) {
            handler.getPlayer().getInventory().offer(new ItemStack(TATER_BOX), true);
        }
    }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registry.ITEM, NucleoidExtras.identifier(id), item);
    }
}
