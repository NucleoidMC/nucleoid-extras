package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.item.*;
import xyz.nucleoid.extras.lobby.item.tater.CreativeTaterBoxItem;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class NEItems {
    private static final List<Item> TATERS = new ArrayList<>();

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
        .displayName(Text.translatable("text.nucleoid_extras.name"))
        .icon(() -> new ItemStack(NEItems.NUCLEOID_LOGO))
        .entries((context, entries) -> {
            entries.add(NEItems.QUICK_ARMOR_STAND);
            entries.add(NEItems.END_PORTAL);
            entries.add(NEItems.END_GATEWAY);
            entries.add(NEItems.SAFE_TNT);
            entries.add(NEItems.LAUNCH_FEATHER);
            entries.add(NEItems.GOLD_LAUNCH_PAD);
            entries.add(NEItems.IRON_LAUNCH_PAD);
            entries.add(NEItems.CONTRIBUTOR_STATUE);
            entries.add(NEItems.INFINITE_DISPENSER);
            entries.add(NEItems.INFINITE_DROPPER);
            entries.add(NEItems.SNAKE_BLOCK);
            entries.add(NEItems.FAST_SNAKE_BLOCK);
            entries.add(NEItems.TRANSIENT_IRON_DOOR);
            entries.add(NEItems.TRANSIENT_OAK_DOOR);
            entries.add(NEItems.TRANSIENT_SPRUCE_DOOR);
            entries.add(NEItems.TRANSIENT_BIRCH_DOOR);
            entries.add(NEItems.TRANSIENT_JUNGLE_DOOR);
            entries.add(NEItems.TRANSIENT_ACACIA_DOOR);
            entries.add(NEItems.TRANSIENT_CHERRY_DOOR);
            entries.add(NEItems.TRANSIENT_DARK_OAK_DOOR);
            entries.add(NEItems.TRANSIENT_MANGROVE_DOOR);
            entries.add(NEItems.TRANSIENT_BAMBOO_DOOR);
            entries.add(NEItems.TRANSIENT_CRIMSON_DOOR);
            entries.add(NEItems.TRANSIENT_WARPED_DOOR);
            entries.add(NEItems.BLACK_CONCRETE_POWDER);
            entries.add(NEItems.BLUE_CONCRETE_POWDER);
            entries.add(NEItems.BROWN_CONCRETE_POWDER);
            entries.add(NEItems.CYAN_CONCRETE_POWDER);
            entries.add(NEItems.GREEN_CONCRETE_POWDER);
            entries.add(NEItems.GRAY_CONCRETE_POWDER);
            entries.add(NEItems.LIGHT_BLUE_CONCRETE_POWDER);
            entries.add(NEItems.LIGHT_GRAY_CONCRETE_POWDER);
            entries.add(NEItems.LIME_CONCRETE_POWDER);
            entries.add(NEItems.MAGENTA_CONCRETE_POWDER);
            entries.add(NEItems.ORANGE_CONCRETE_POWDER);
            entries.add(NEItems.PINK_CONCRETE_POWDER);
            entries.add(NEItems.PURPLE_CONCRETE_POWDER);
            entries.add(NEItems.RED_CONCRETE_POWDER);
            entries.add(NEItems.WHITE_CONCRETE_POWDER);
            entries.add(NEItems.YELLOW_CONCRETE_POWDER);
            entries.add(NEItems.GAME_PORTAL_OPENER);
            entries.add(NEItems.TATER_BOX);
            entries.add(NEItems.CREATIVE_TATER_BOX);
            TATERS.forEach(entries::add);
        })
        .build();

    public static final Item NUCLEOID_LOGO = createHead(NEBlocks.NUCLEOID_LOGO);
    public static final Item NUCLE_PAST_LOGO = createHead(NEBlocks.NUCLE_PAST_LOGO);

    public static final Item END_PORTAL = createSimple(NEBlocks.END_PORTAL, Items.BLACK_CARPET);
    public static final Item END_GATEWAY = createSimple(NEBlocks.END_GATEWAY, Items.BLACK_WOOL);
    public static final Item SAFE_TNT = createSimple(NEBlocks.SAFE_TNT, Items.TNT);

    public static final Item GOLD_LAUNCH_PAD = createSimple(NEBlocks.GOLD_LAUNCH_PAD, Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Item IRON_LAUNCH_PAD = createSimple(NEBlocks.IRON_LAUNCH_PAD, Items.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final Item CONTRIBUTOR_STATUE = createSimple(NEBlocks.CONTRIBUTOR_STATUE, Items.SMOOTH_STONE);

    public static final Item INFINITE_DISPENSER = createSimple(NEBlocks.INFINITE_DISPENSER, Items.DISPENSER);
    public static final Item INFINITE_DROPPER = createSimple(NEBlocks.INFINITE_DROPPER, Items.DROPPER);
    public static final Item SNAKE_BLOCK = createSimple(NEBlocks.SNAKE_BLOCK, Items.LIME_CONCRETE);
    public static final Item FAST_SNAKE_BLOCK = createSimple(NEBlocks.FAST_SNAKE_BLOCK, Items.LIGHT_BLUE_CONCRETE);

    public static final Item TRANSIENT_IRON_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_IRON_DOOR, new Item.Settings(), Items.IRON_DOOR);
    public static final Item TRANSIENT_OAK_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_OAK_DOOR, new Item.Settings(), Items.OAK_DOOR);
    public static final Item TRANSIENT_SPRUCE_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_SPRUCE_DOOR, new Item.Settings(), Items.SPRUCE_DOOR);
    public static final Item TRANSIENT_BIRCH_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_BIRCH_DOOR, new Item.Settings(), Items.BIRCH_DOOR);
    public static final Item TRANSIENT_JUNGLE_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_JUNGLE_DOOR, new Item.Settings(), Items.JUNGLE_DOOR);
    public static final Item TRANSIENT_ACACIA_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_ACACIA_DOOR, new Item.Settings(), Items.ACACIA_DOOR);
    public static final Item TRANSIENT_CHERRY_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_CHERRY_DOOR, new Item.Settings(), Items.CHERRY_DOOR);
    public static final Item TRANSIENT_DARK_OAK_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_DARK_OAK_DOOR, new Item.Settings(), Items.DARK_OAK_DOOR);
    public static final Item TRANSIENT_MANGROVE_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_MANGROVE_DOOR, new Item.Settings(), Items.MANGROVE_DOOR);
    public static final Item TRANSIENT_BAMBOO_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_BAMBOO_DOOR, new Item.Settings(), Items.BAMBOO_DOOR);
    public static final Item TRANSIENT_CRIMSON_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_CRIMSON_DOOR, new Item.Settings(), Items.CRIMSON_DOOR);
    public static final Item TRANSIENT_WARPED_DOOR = new LobbyTallBlockItem(NEBlocks.TRANSIENT_WARPED_DOOR, new Item.Settings(), Items.WARPED_DOOR);

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
    public static final Item BOTANICAL_TINY_POTATO = createHead(NEBlocks.BOTANICAL_TINY_POTATO);
    public static final Item IRRITATER = createHead(NEBlocks.IRRITATER);
    public static final Item SAD_TATER = createHead(NEBlocks.SAD_TATER);
    public static final Item FLOWERING_AZALEA_TATER = createHead(NEBlocks.FLOWERING_AZALEA_TATER);
    public static final Item STONE_TATER = createHead(NEBlocks.STONE_TATER);
    public static final Item CALCITE_TATER = createHead(NEBlocks.CALCITE_TATER);
    public static final Item TUFF_TATER = createHead(NEBlocks.TUFF_TATER);
    public static final Item BASALT_TATER = createHead(NEBlocks.BASALT_TATER);
    public static final Item DRIPSTONE_TATER = createHead(NEBlocks.DRIPSTONE_TATER);
    public static final Item AMETHYST_TATER = createHead(NEBlocks.AMETHYST_TATER);
    public static final Item PACKED_ICE_TATER = createHead(NEBlocks.PACKED_ICE_TATER);
    public static final Item BLUE_ICE_TATER = createHead(NEBlocks.BLUE_ICE_TATER);
    public static final Item FLAME_TATER = createHead(NEBlocks.FLAME_TATER);
    public static final Item PUZZLE_CUBE_TATER = createHead(NEBlocks.PUZZLE_CUBE_TATER);
    public static final Item LUCKY_TATER = createHead(NEBlocks.LUCKY_TATER);
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
    public static final Item VIRAL_TATER = createHead(NEBlocks.VIRAL_TATER);
    public static final Item TATEROID = createHead(NEBlocks.TATEROID);
    public static final Item RED_TATEROID = createHead(NEBlocks.RED_TATEROID);
    public static final Item ORANGE_TATEROID = createHead(NEBlocks.ORANGE_TATEROID);
    public static final Item YELLOW_TATEROID = createHead(NEBlocks.YELLOW_TATEROID);
    public static final Item GREEN_TATEROID = createHead(NEBlocks.GREEN_TATEROID);
    public static final Item BLUE_TATEROID = createHead(NEBlocks.BLUE_TATEROID);
    public static final Item PURPLE_TATEROID = createHead(NEBlocks.PURPLE_TATEROID);
    public static final Item FLIPPED_TATER = createHead(NEBlocks.FLIPPED_TATER);
    public static final Item BACKWARD_TATER = createHead(NEBlocks.BACKWARD_TATER);
    public static final Item UPWARD_TATER = createHead(NEBlocks.UPWARD_TATER);
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

    public static final Item COAL_TATER = createHead(NEBlocks.COAL_TATER);
    public static final Item DIAMOND_TATER = createHead(NEBlocks.DIAMOND_TATER);
    public static final Item EMERALD_TATER = createHead(NEBlocks.EMERALD_TATER);
    public static final Item GOLD_TATER = createHead(NEBlocks.GOLD_TATER);
    public static final Item IRON_TATER = createHead(NEBlocks.IRON_TATER);
    public static final Item LAPIS_TATER = createHead(NEBlocks.LAPIS_TATER);
    public static final Item NETHERITE_TATER = createHead(NEBlocks.NETHERITE_TATER);
    public static final Item QUARTZ_TATER = createHead(NEBlocks.QUARTZ_TATER);
    public static final Item REDSTONE_TATER = createHead(NEBlocks.REDSTONE_TATER);

    public static final Item COPPER_TATER = createHead(NEBlocks.COPPER_TATER);
    public static final Item EXPOSED_COPPER_TATER = createHead(NEBlocks.EXPOSED_COPPER_TATER);
    public static final Item WEATHERED_COPPER_TATER = createHead(NEBlocks.WEATHERED_COPPER_TATER);
    public static final Item OXIDIZED_COPPER_TATER = createHead(NEBlocks.OXIDIZED_COPPER_TATER);

    public static final Item CAKE_TATER = createHead(NEBlocks.CAKE_TATER);
    public static final Item ENDERTATER = createHead(NEBlocks.ENDERTATER);
    public static final Item FURNACE_TATER = createHead(NEBlocks.FURNACE_TATER);
    public static final Item MELON_TATER = createHead(NEBlocks.MELON_TATER);
    public static final Item PUMPKIN_TATER = createHead(NEBlocks.PUMPKIN_TATER);
    public static final Item JACK_O_TATER = createHead(NEBlocks.JACK_O_TATER);
    public static final Item SCULK_TATER = createHead(NEBlocks.SCULK_TATER);
    public static final Item SLIME_TATER = createHead(NEBlocks.SLIME_TATER);
    public static final Item HEROBRINE_TATER = createHead(NEBlocks.HEROBRINE_TATER);
    public static final Item OCHRE_FROGLIGHT_TATER = createHead(NEBlocks.OCHRE_FROGLIGHT_TATER);
    public static final Item PEARLESCENT_FROGLIGHT_TATER = createHead(NEBlocks.PEARLESCENT_FROGLIGHT_TATER);
    public static final Item VERDANT_FROGLIGHT_TATER = createHead(NEBlocks.VERDANT_FROGLIGHT_TATER);
    public static final Item SNOWMAN_TATER = createHead(NEBlocks.SNOWMAN_TATER);

    public static final Item ACACIA_TATER = createHead(NEBlocks.ACACIA_TATER);
    public static final Item ANDESITE_TATER = createHead(NEBlocks.ANDESITE_TATER);
    public static final Item BAMBOO_TATER = createHead(NEBlocks.BAMBOO_TATER);
    public static final Item BARRIER_TATER = createHead(NEBlocks.BARRIER_TATER);
    public static final Item BEDROCK_TATER = createHead(NEBlocks.BEDROCK_TATER);
    public static final Item BIRCH_TATER = createHead(NEBlocks.BIRCH_TATER);
    public static final Item BONE_TATER = createHead(NEBlocks.BONE_TATER);
    public static final Item BRAIN_CORAL_TATER = createHead(NEBlocks.BRAIN_CORAL_TATER);
    public static final Item BRICK_TATER = createHead(NEBlocks.BRICK_TATER);
    public static final Item BUBBLE_CORAL_TATER = createHead(NEBlocks.BUBBLE_CORAL_TATER);
    public static final Item CACTUS_TATER = createHead(NEBlocks.CACTUS_TATER);
    public static final Item CHORUS_TATER = createHead(NEBlocks.CHORUS_TATER);
    public static final Item CLAY_TATER = createHead(NEBlocks.CLAY_TATER);
    public static final Item CRIMSON_TATER = createHead(NEBlocks.CRIMSON_TATER);
    public static final Item DARK_OAK_TATER = createHead(NEBlocks.DARK_OAK_TATER);
    public static final Item DARK_PRISMARINE_TATER = createHead(NEBlocks.DARK_PRISMARINE_TATER);
    public static final Item DIORITE_TATER = createHead(NEBlocks.DIORITE_TATER);
    public static final Item DIRT_TATER = createHead(NEBlocks.DIRT_TATER);
    public static final Item END_STONE_TATER = createHead(NEBlocks.END_STONE_TATER);
    public static final Item FIRE_CORAL_TATER = createHead(NEBlocks.FIRE_CORAL_TATER);
    public static final Item GRANITE_TATER = createHead(NEBlocks.GRANITE_TATER);
    public static final Item GRASS_TATER = createHead(NEBlocks.GRASS_TATER);
    public static final Item HAY_TATER = createHead(NEBlocks.HAY_TATER);
    public static final Item HONEY_TATER = createHead(NEBlocks.HONEY_TATER);
    public static final Item HONEYCOMB_TATER = createHead(NEBlocks.HONEYCOMB_TATER);
    public static final Item HORN_CORAL_TATER = createHead(NEBlocks.HORN_CORAL_TATER);
    public static final Item JUNGLE_TATER = createHead(NEBlocks.JUNGLE_TATER);
    public static final Item LIGHT_TATER = createHead(NEBlocks.LIGHT_TATER);
    public static final Item MYCELIUM_TATER = createHead(NEBlocks.MYCELIUM_TATER);
    public static final Item NETHER_WART_TATER = createHead(NEBlocks.NETHER_WART_TATER);
    public static final Item OAK_TATER = createHead(NEBlocks.OAK_TATER);
    public static final Item OBSIDIAN_TATER = createHead(NEBlocks.OBSIDIAN_TATER);
    public static final Item PODZOL_TATER = createHead(NEBlocks.PODZOL_TATER);
    public static final Item PRISMARINE_BRICK_TATER = createHead(NEBlocks.PRISMARINE_BRICK_TATER);
    public static final Item PRISMARINE_TATER = createHead(NEBlocks.PRISMARINE_TATER);
    public static final Item RED_SAND_TATER = createHead(NEBlocks.RED_SAND_TATER);
    public static final Item SAND_TATER = createHead(NEBlocks.SAND_TATER);
    public static final Item SEA_LANTERN_TATER = createHead(NEBlocks.SEA_LANTERN_TATER);
    public static final Item SHROOMLIGHT_TATER = createHead(NEBlocks.SHROOMLIGHT_TATER);
    public static final Item SHULKER_TATER = createHead(NEBlocks.SHULKER_TATER);
    public static final Item SMOOTH_STONE_TATER = createHead(NEBlocks.SMOOTH_STONE_TATER);
    public static final Item SOUL_SAND_TATER = createHead(NEBlocks.SOUL_SAND_TATER);
    public static final Item SPONGE_TATER = createHead(NEBlocks.SPONGE_TATER);
    public static final Item SPRUCE_TATER = createHead(NEBlocks.SPRUCE_TATER);
    public static final Item STONE_BRICK_TATER = createHead(NEBlocks.STONE_BRICK_TATER);
    public static final Item STRUCTURE_VOID_TATER = createHead(NEBlocks.STRUCTURE_VOID_TATER);
    public static final Item TARGET_TATER = createHead(NEBlocks.TARGET_TATER);
    public static final Item TERRACOTTA_TATER = createHead(NEBlocks.TERRACOTTA_TATER);
    public static final Item TNTATER = createHead(NEBlocks.TNTATER);
    public static final Item TUBE_CORAL_TATER = createHead(NEBlocks.TUBE_CORAL_TATER);
    public static final Item WARPED_TATER = createHead(NEBlocks.WARPED_TATER);
    public static final Item WARPED_WART_TATER = createHead(NEBlocks.WARPED_WART_TATER);
    public static final Item WOOL_TATER = createHead(NEBlocks.WOOL_TATER);

    public static final Item ACACIA_LOG_TATER = createHead(NEBlocks.ACACIA_LOG_TATER);
    public static final Item ANGRY_BEE_TATER = createHead(NEBlocks.ANGRY_BEE_TATER);
    public static final Item BEACON_TATER = createHead(NEBlocks.BEACON_TATER);
    public static final Item BEE_NEST_TATER = createHead(NEBlocks.BEE_NEST_TATER);
    public static final Item BEE_TATER = createHead(NEBlocks.BEE_TATER);
    public static final Item BEEHIVE_TATER = createHead(NEBlocks.BEEHIVE_TATER);
    public static final Item BIRCH_LOG_TATER = createHead(NEBlocks.BIRCH_LOG_TATER);
    public static final Item BLACKSTONE_TATER = createHead(NEBlocks.BLACKSTONE_TATER);
    public static final Item BLAZE_TATER = createHead(NEBlocks.BLAZE_TATER);
    public static final Item BOOKSHELF_TATER = createHead(NEBlocks.BOOKSHELF_TATER);
    public static final Item BROWN_MOOSHROOM_TATER = createHead(NEBlocks.BROWN_MOOSHROOM_TATER);
    public static final Item BROWN_MUSHROOM_TATER = createHead(NEBlocks.BROWN_MUSHROOM_TATER);
    public static final Item CAVE_SPIDER_TATER = createHead(NEBlocks.CAVE_SPIDER_TATER);
    public static final Item COBBLED_DEEPSLATE_TATER = createHead(NEBlocks.COBBLED_DEEPSLATE_TATER);
    public static final Item COBBLESTONE_TATER = createHead(NEBlocks.COBBLESTONE_TATER);
    public static final Item COCOA_TATER = createHead(NEBlocks.COCOA_TATER);
    public static final Item COLD_STRIDER_TATER = createHead(NEBlocks.COLD_STRIDER_TATER);
    public static final Item COW_TATER = createHead(NEBlocks.COW_TATER);
    public static final Item CRAFTING_TATER = createHead(NEBlocks.CRAFTING_TATER);
    public static final Item CRIMSON_NYLIUM_TATER = createHead(NEBlocks.CRIMSON_NYLIUM_TATER);
    public static final Item CRIMSON_STEM_TATER = createHead(NEBlocks.CRIMSON_STEM_TATER);
    public static final Item DARK_OAK_LOG_TATER = createHead(NEBlocks.DARK_OAK_LOG_TATER);
    public static final Item DAYLIGHT_DETECTOR_TATER = createHead(NEBlocks.DAYLIGHT_DETECTOR_TATER);
    public static final Item DEAD_BRAIN_CORAL_TATER = createHead(NEBlocks.DEAD_BRAIN_CORAL_TATER);
    public static final Item DEAD_BUBBLE_CORAL_TATER = createHead(NEBlocks.DEAD_BUBBLE_CORAL_TATER);
    public static final Item DEAD_FIRE_CORAL_TATER = createHead(NEBlocks.DEAD_FIRE_CORAL_TATER);
    public static final Item DEAD_HORN_CORAL_TATER = createHead(NEBlocks.DEAD_HORN_CORAL_TATER);
    public static final Item DEAD_TUBE_CORAL_TATER = createHead(NEBlocks.DEAD_TUBE_CORAL_TATER);
    public static final Item DEEPSLATE_BRICK_TATER = createHead(NEBlocks.DEEPSLATE_BRICK_TATER);
    public static final Item DEEPSLATE_TATER = createHead(NEBlocks.DEEPSLATE_TATER);
    public static final Item DRIED_KELP_TATER = createHead(NEBlocks.DRIED_KELP_TATER);
    public static final Item DROWNED_TATER = createHead(NEBlocks.DROWNED_TATER);
    public static final Item EYE_OF_ENDER_TATER = createHead(NEBlocks.EYE_OF_ENDER_TATER);
    public static final Item FOX_TATER = createHead(NEBlocks.FOX_TATER);
    public static final Item GHAST_TATER = createHead(NEBlocks.GHAST_TATER);
    public static final Item GILDED_BLACKSTONE_TATER = createHead(NEBlocks.GILDED_BLACKSTONE_TATER);
    public static final Item GLOW_SQUID_TATER = createHead(NEBlocks.GLOW_SQUID_TATER);
    public static final Item GLOWSTONE_TATER = createHead(NEBlocks.GLOWSTONE_TATER);
    public static final Item HUSK_TATER = createHead(NEBlocks.HUSK_TATER);
    public static final Item INVERTED_DAYLIGHT_DETECTOR_TATER = createHead(NEBlocks.INVERTED_DAYLIGHT_DETECTOR_TATER);
    public static final Item JUNGLE_LOG_TATER = createHead(NEBlocks.JUNGLE_LOG_TATER);
    public static final Item MAGMA_CUBE_TATER = createHead(NEBlocks.MAGMA_CUBE_TATER);
    public static final Item MOOBLOOM_TATER = createHead(NEBlocks.MOOBLOOM_TATER);
    public static final Item MOOLIP_TATER = createHead(NEBlocks.MOOLIP_TATER);
    public static final Item MUDDY_PIG_TATER = createHead(NEBlocks.MUDDY_PIG_TATER);
    public static final Item MUSHROOM_STEM_TATER = createHead(NEBlocks.MUSHROOM_STEM_TATER);
    public static final Item NETHER_BRICK_TATER = createHead(NEBlocks.NETHER_BRICK_TATER);
    public static final Item NETHERRACK_TATER = createHead(NEBlocks.NETHERRACK_TATER);
    public static final Item OAK_LOG_TATER = createHead(NEBlocks.OAK_LOG_TATER);
    public static final Item PIG_TATER = createHead(NEBlocks.PIG_TATER);
    public static final Item POLAR_BEAR_TATER = createHead(NEBlocks.POLAR_BEAR_TATER);
    public static final Item PUFFERTATER = createHead(NEBlocks.PUFFERTATER);
    public static final Item RED_MOOSHROOM_TATER = createHead(NEBlocks.RED_MOOSHROOM_TATER);
    public static final Item RED_MUSHROOM_TATER = createHead(NEBlocks.RED_MUSHROOM_TATER);
    public static final Item RED_NETHER_BRICK_TATER = createHead(NEBlocks.RED_NETHER_BRICK_TATER);
    public static final Item RED_SANDSTONE_TATER = createHead(NEBlocks.RED_SANDSTONE_TATER);
    public static final Item RUBY_TATER = createHead(NEBlocks.RUBY_TATER);
    public static final Item SANDSTONE_TATER = createHead(NEBlocks.SANDSTONE_TATER);
    public static final Item SEA_PICKLE_TATER = createHead(NEBlocks.SEA_PICKLE_TATER);
    public static final Item SHEEP_TATER = createHead(NEBlocks.SHEEP_TATER);
    public static final Item SHIELDED_WITHER_TATER = createHead(NEBlocks.SHIELDED_WITHER_TATER);
    public static final Item SNOW_FOX_TATER = createHead(NEBlocks.SNOW_FOX_TATER);
    public static final Item SPIDER_TATER = createHead(NEBlocks.SPIDER_TATER);
    public static final Item SPRUCE_LOG_TATER = createHead(NEBlocks.SPRUCE_LOG_TATER);
    public static final Item SQUID_TATER = createHead(NEBlocks.SQUID_TATER);
    public static final Item STRAY_TATER = createHead(NEBlocks.STRAY_TATER);
    public static final Item STRIDER_TATER = createHead(NEBlocks.STRIDER_TATER);
    public static final Item TURTLE_EGG_TATER = createHead(NEBlocks.TURTLE_EGG_TATER);
    public static final Item WARPED_NYLIUM_TATER = createHead(NEBlocks.WARPED_NYLIUM_TATER);
    public static final Item WARPED_STEM_TATER = createHead(NEBlocks.WARPED_STEM_TATER);
    public static final Item WITHER_TATER = createHead(NEBlocks.WITHER_TATER);

    public static final Item AZALEA_TATER = createHead(NEBlocks.AZALEA_TATER);
    public static final Item BELL_TATER = createHead(NEBlocks.BELL_TATER);
    public static final Item COLD_FROG_TATER = createHead(NEBlocks.COLD_FROG_TATER);
    public static final Item CONDUIT_TATER = createHead(NEBlocks.CONDUIT_TATER);
    public static final Item ELDER_GUARDIAN_TATER = createHead(NEBlocks.ELDER_GUARDIAN_TATER);
    public static final Item END_STONE_BRICK_TATER = createHead(NEBlocks.END_STONE_BRICK_TATER);
    public static final Item FLOWER_POT_TATER = createHead(NEBlocks.FLOWER_POT_TATER);
    public static final Item GUARDIAN_TATER = createHead(NEBlocks.GUARDIAN_TATER);
    public static final Item ILLAGER_TATER = createHead(NEBlocks.ILLAGER_TATER);
    public static final Item ILLUSIONER_TATER = createHead(NEBlocks.ILLUSIONER_TATER);
    public static final Item JUKEBOX_TATER = createHead(NEBlocks.JUKEBOX_TATER);
    public static final Item LANTERN_TATER = createHead(NEBlocks.LANTERN_TATER);
    public static final Item PIGLIN_TATER = createHead(NEBlocks.PIGLIN_TATER);
    public static final Item PINK_WITHER_TATER = createHead(NEBlocks.PINK_WITHER_TATER);
    public static final Item PISTON_TATER = createHead(NEBlocks.PISTON_TATER);
    public static final Item PURPUR_TATER = createHead(NEBlocks.PURPUR_TATER);
    public static final Item SOUL_LANTERN_TATER = createHead(NEBlocks.SOUL_LANTERN_TATER);
    public static final Item SOUL_SOIL_TATER = createHead(NEBlocks.SOUL_SOIL_TATER);
    public static final Item STICKY_PISTON_TATER = createHead(NEBlocks.STICKY_PISTON_TATER);
    public static final Item TEMPERATE_FROG_TATER = createHead(NEBlocks.TEMPERATE_FROG_TATER);
    public static final Item UNDERWATER_TNTATER = createHead(NEBlocks.UNDERWATER_TNTATER);
    public static final Item VEX_TATER = createHead(NEBlocks.VEX_TATER);
    public static final Item VILLAGER_TATER = createHead(NEBlocks.VILLAGER_TATER);
    public static final Item VINDITATER = createHead(NEBlocks.VINDITATER);
    public static final Item WANDERING_TRADER_TATER = createHead(NEBlocks.WANDERING_TRADER_TATER);
    public static final Item WARM_FROG_TATER = createHead(NEBlocks.WARM_FROG_TATER);
    public static final Item WAX_TATER = createHead(NEBlocks.WAX_TATER);
    public static final Item WITCH_TATER = createHead(NEBlocks.WITCH_TATER);
    public static final Item ZOMBIE_VILLAGER_TATER = createHead(NEBlocks.ZOMBIE_VILLAGER_TATER);
    public static final Item ZOMBIFIED_PIGLIN_TATER = createHead(NEBlocks.ZOMBIFIED_PIGLIN_TATER);

    public static final Item BONE_SPIDER_TATER = createHead(NEBlocks.BONE_SPIDER_TATER);
    public static final Item BOULDERING_ZOMBIE_TATER = createHead(NEBlocks.BOULDERING_ZOMBIE_TATER);
    public static final Item CHARGED_CREEPER_TATER = createHead(NEBlocks.CHARGED_CREEPER_TATER);
    public static final Item LOBBER_ZOMBIE_TATER = createHead(NEBlocks.LOBBER_ZOMBIE_TATER);
    public static final Item MOSSY_SKELETATER = createHead(NEBlocks.MOSSY_SKELETATER);
    public static final Item STRIPPED_ACACIA_LOG_TATER = createHead(NEBlocks.STRIPPED_ACACIA_LOG_TATER);
    public static final Item STRIPPED_BIRCH_LOG_TATER = createHead(NEBlocks.STRIPPED_BIRCH_LOG_TATER);
    public static final Item STRIPPED_CRIMSON_STEM_TATER = createHead(NEBlocks.STRIPPED_CRIMSON_STEM_TATER);
    public static final Item STRIPPED_DARK_OAK_LOG_TATER = createHead(NEBlocks.STRIPPED_DARK_OAK_LOG_TATER);
    public static final Item STRIPPED_JUNGLE_LOG_TATER = createHead(NEBlocks.STRIPPED_JUNGLE_LOG_TATER);
    public static final Item STRIPPED_OAK_LOG_TATER = createHead(NEBlocks.STRIPPED_OAK_LOG_TATER);
    public static final Item STRIPPED_SPRUCE_LOG_TATER = createHead(NEBlocks.STRIPPED_SPRUCE_LOG_TATER);
    public static final Item STRIPPED_WARPED_STEM_TATER = createHead(NEBlocks.STRIPPED_WARPED_STEM_TATER);
    public static final Item TROPICAL_SLIME_TATER = createHead(NEBlocks.TROPICAL_SLIME_TATER);

    public static final Item APPLE_TATER = createHead(NEBlocks.APPLE_TATER);
    public static final Item GOLDEN_APPLE_TATER = createHead(NEBlocks.GOLDEN_APPLE_TATER);
    public static final Item ICE_TATER = createHead(NEBlocks.ICE_TATER);
    public static final Item KING_TATER = createHead(NEBlocks.KING_TATER);
    public static final Item RAW_COPPER_TATER = createHead(NEBlocks.RAW_COPPER_TATER);
    public static final Item RAW_GOLD_TATER = createHead(NEBlocks.RAW_GOLD_TATER);
    public static final Item RAW_IRON_TATER = createHead(NEBlocks.RAW_IRON_TATER);

    public static final Item ALLAY_TATER = createHead(NEBlocks.ALLAY_TATER);
    public static final Item MANGROVE_LOG_TATER = createHead(NEBlocks.MANGROVE_LOG_TATER);
    public static final Item MANGROVE_TATER = createHead(NEBlocks.MANGROVE_TATER);
    public static final Item MUD_BRICK_TATER = createHead(NEBlocks.MUD_BRICK_TATER);
    public static final Item MUD_TATER = createHead(NEBlocks.MUD_TATER);
    public static final Item PACKED_MUD_TATER = createHead(NEBlocks.PACKED_MUD_TATER);
    public static final Item STRIPPED_MANGROVE_LOG_TATER = createHead(NEBlocks.STRIPPED_MANGROVE_LOG_TATER);

    public static final Item LUCY_AXOLOTL_TATER = createHead(NEBlocks.LUCY_AXOLOTL_TATER);
    public static final Item WILD_AXOLOTL_TATER = createHead(NEBlocks.WILD_AXOLOTL_TATER);
    public static final Item GOLD_AXOLOTL_TATER = createHead(NEBlocks.GOLD_AXOLOTL_TATER);
    public static final Item CYAN_AXOLOTL_TATER = createHead(NEBlocks.CYAN_AXOLOTL_TATER);
    public static final Item BLUE_AXOLOTL_TATER = createHead(NEBlocks.BLUE_AXOLOTL_TATER);

    public static final Item BRONZE_CAPSULE_TATER = createHead(NEBlocks.BRONZE_CAPSULE_TATER);
    public static final Item SILVER_CAPSULE_TATER = createHead(NEBlocks.SILVER_CAPSULE_TATER);
    public static final Item GOLD_CAPSULE_TATER = createHead(NEBlocks.GOLD_CAPSULE_TATER);

    public static final Item CORRUPTATER = createHead(NEBlocks.CORRUPTATER);

    public static final Item TATER_BOX = new TaterBoxItem(new Item.Settings().maxDamage(0));
    public static final Item CREATIVE_TATER_BOX = new CreativeTaterBoxItem(new Item.Settings().maxDamage(0));

    public static final Item QUICK_ARMOR_STAND = new QuickArmorStandItem(new Item.Settings());
    public static final Item GAME_PORTAL_OPENER = new GamePortalOpenerItem(new Item.Settings().maxCount(1));
    public static final Item LAUNCH_FEATHER = new LaunchFeatherItem(new Item.Settings().maxCount(1));

    private static Item createHead(Block head) {
        if (head instanceof TinyPotatoBlock tinyPotatoBlock) {
            return new LobbyHeadItem(head, new Item.Settings(), tinyPotatoBlock.getItemTexture());
        } else if (head instanceof PolymerHeadBlock headBlock) {
            return new LobbyHeadItem(head, new Item.Settings(), headBlock.getPolymerSkinValue(head.getDefaultState(), BlockPos.ORIGIN, null));
        }

        return createSimple(head, Items.STONE);
    }

    private static Item createSimple(Block block, Item virtual) {
        return new LobbyBlockItem(block, new Item.Settings(), virtual);
    }

    public static void register() {
        register("end_portal", END_PORTAL);
        register("end_gateway", END_GATEWAY);
        register("safe_tnt", SAFE_TNT);
        register("gold_launch_pad", GOLD_LAUNCH_PAD);
        register("iron_launch_pad", IRON_LAUNCH_PAD);
        register("contributor_statue", CONTRIBUTOR_STATUE);
        register("infinite_dispenser", INFINITE_DISPENSER);
        register("infinite_dropper", INFINITE_DROPPER);
        register("snake_block", SNAKE_BLOCK);
        register("fast_snake_block", FAST_SNAKE_BLOCK);

        register("transient_iron_door", TRANSIENT_IRON_DOOR);
        register("transient_oak_door", TRANSIENT_OAK_DOOR);
        register("transient_spruce_door", TRANSIENT_SPRUCE_DOOR);
        register("transient_birch_door", TRANSIENT_BIRCH_DOOR);
        register("transient_jungle_door", TRANSIENT_JUNGLE_DOOR);
        register("transient_acacia_door", TRANSIENT_ACACIA_DOOR);
        register("transient_cherry_door", TRANSIENT_CHERRY_DOOR);
        register("transient_dark_oak_door", TRANSIENT_DARK_OAK_DOOR);
        register("transient_mangrove_door", TRANSIENT_MANGROVE_DOOR);
        register("transient_bamboo_door", TRANSIENT_BAMBOO_DOOR);
        register("transient_crimson_door", TRANSIENT_CRIMSON_DOOR);
        register("transient_warped_door", TRANSIENT_WARPED_DOOR);

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
        registerTater("nucleoid_logo", NUCLEOID_LOGO);
        registerTater("nucle_past_logo", NUCLE_PAST_LOGO);

        registerTater("tiny_potato", TINY_POTATO);
        registerTater("botanical_potato", BOTANICAL_TINY_POTATO);
        registerTater("irritater", IRRITATER);
        registerTater("sad_tater", SAD_TATER);
        registerTater("flowering_azalea_tater", FLOWERING_AZALEA_TATER);
        registerTater("stone_tater", STONE_TATER);
        registerTater("calcite_tater", CALCITE_TATER);
        registerTater("tuff_tater", TUFF_TATER);
        registerTater("basalt_tater", BASALT_TATER);
        registerTater("dripstone_tater", DRIPSTONE_TATER);
        registerTater("amethyst_tater", AMETHYST_TATER);
        registerTater("packed_ice_tater", PACKED_ICE_TATER);
        registerTater("blue_ice_tater", BLUE_ICE_TATER);
        registerTater("flame_tater", FLAME_TATER);

        registerTater("puzzle_cube_tater", PUZZLE_CUBE_TATER);
        registerTater("lucky_tater", LUCKY_TATER);
        registerTater("crate_tater", CRATE_TATER);
        registerTater("tater_of_undying", TATER_OF_UNDYING);
        registerTater("crying_obsidian_tater", CRYING_OBSIDIAN_TATER);

        registerTater("dice_tater", DICE_TATER);
        registerTater("trans_tater", TRANS_TATER);
        registerTater("asexual_tater", ASEXUAL_TATER);
        registerTater("bi_tater", BI_TATER);
        registerTater("gay_tater", GAY_TATER);
        registerTater("lesbian_tater", LESBIAN_TATER);
        registerTater("nonbinary_tater", NONBINARY_TATER);
        registerTater("pan_tater", PAN_TATER);
        registerTater("warden_tater", WARDEN_TATER);
        registerTater("viral_tater", VIRAL_TATER);
        registerTater("tateroid", TATEROID);
        registerTater("red_tateroid", RED_TATEROID);
        registerTater("orange_tateroid", ORANGE_TATEROID);
        registerTater("yellow_tateroid", YELLOW_TATEROID);
        registerTater("green_tateroid", GREEN_TATEROID);
        registerTater("blue_tateroid", BLUE_TATEROID);
        registerTater("purple_tateroid", PURPLE_TATEROID);
        registerTater("flipped_tater", FLIPPED_TATER);
        registerTater("backward_tater", BACKWARD_TATER);
        registerTater("upward_tater", UPWARD_TATER);
        registerTater("santa_hat_tater", SANTA_HAT_TATER);
        registerTater("genderfluid_tater", GENDERFLUID_TATER);
        registerTater("demisexual_tater", DEMISEXUAL_TATER);

        registerTater("skeletater", SKELETATER);
        registerTater("wither_skeletater", WITHER_SKELETATER);
        registerTater("zombie_tater", ZOMBIE_TATER);
        registerTater("creeper_tater", CREEPER_TATER);
        registerTater("steve_tater", STEVE_TATER);
        registerTater("alex_tater", ALEX_TATER);

        registerTater("white_tater", WHITE_TATER);
        registerTater("orange_tater", ORANGE_TATER);
        registerTater("magenta_tater", MAGENTA_TATER);
        registerTater("light_blue_tater", LIGHT_BLUE_TATER);
        registerTater("yellow_tater", YELLOW_TATER);
        registerTater("lime_tater", LIME_TATER);
        registerTater("pink_tater", PINK_TATER);
        registerTater("gray_tater", GRAY_TATER);
        registerTater("light_gray_tater", LIGHT_GRAY_TATER);
        registerTater("cyan_tater", CYAN_TATER);
        registerTater("purple_tater", PURPLE_TATER);
        registerTater("blue_tater", BLUE_TATER);
        registerTater("brown_tater", BROWN_TATER);
        registerTater("green_tater", GREEN_TATER);
        registerTater("red_tater", RED_TATER);
        registerTater("black_tater", BLACK_TATER);

        registerTater("coal_tater", COAL_TATER);
        registerTater("diamond_tater", DIAMOND_TATER);
        registerTater("emerald_tater", EMERALD_TATER);
        registerTater("gold_tater", GOLD_TATER);
        registerTater("iron_tater", IRON_TATER);
        registerTater("lapis_tater", LAPIS_TATER);
        registerTater("netherite_tater", NETHERITE_TATER);
        registerTater("quartz_tater", QUARTZ_TATER);
        registerTater("redstone_tater", REDSTONE_TATER);

        registerTater("copper_tater", COPPER_TATER);
        registerTater("exposed_copper_tater", EXPOSED_COPPER_TATER);
        registerTater("weathered_copper_tater", WEATHERED_COPPER_TATER);
        registerTater("oxidized_copper_tater", OXIDIZED_COPPER_TATER);

        registerTater("cake_tater", CAKE_TATER);
        registerTater("endertater", ENDERTATER);
        registerTater("furnace_tater", FURNACE_TATER);
        registerTater("melon_tater", MELON_TATER);
        registerTater("pumpkin_tater", PUMPKIN_TATER);
        registerTater("jack_o_tater", JACK_O_TATER);
        registerTater("sculk_tater", SCULK_TATER);
        registerTater("slime_tater", SLIME_TATER);
        registerTater("herobrine_tater", HEROBRINE_TATER);
        registerTater("ochre_froglight_tater", OCHRE_FROGLIGHT_TATER);
        registerTater("pearlescent_froglight_tater", PEARLESCENT_FROGLIGHT_TATER);
        registerTater("verdant_froglight_tater", VERDANT_FROGLIGHT_TATER);
        registerTater("snowman_tater", SNOWMAN_TATER);

        registerTater("acacia_tater", ACACIA_TATER);
        registerTater("andesite_tater", ANDESITE_TATER);
        registerTater("bamboo_tater", BAMBOO_TATER);
        registerTater("barrier_tater", BARRIER_TATER);
        registerTater("bedrock_tater", BEDROCK_TATER);
        registerTater("birch_tater", BIRCH_TATER);
        registerTater("bone_tater", BONE_TATER);
        registerTater("brain_coral_tater", BRAIN_CORAL_TATER);
        registerTater("brick_tater", BRICK_TATER);
        registerTater("bubble_coral_tater", BUBBLE_CORAL_TATER);
        registerTater("cactus_tater", CACTUS_TATER);
        registerTater("chorus_tater", CHORUS_TATER);
        registerTater("clay_tater", CLAY_TATER);
        registerTater("crimson_tater", CRIMSON_TATER);
        registerTater("dark_oak_tater", DARK_OAK_TATER);
        registerTater("dark_prismarine_tater", DARK_PRISMARINE_TATER);
        registerTater("diorite_tater", DIORITE_TATER);
        registerTater("dirt_tater", DIRT_TATER);
        registerTater("end_stone_tater", END_STONE_TATER);
        registerTater("fire_coral_tater", FIRE_CORAL_TATER);
        registerTater("granite_tater", GRANITE_TATER);
        registerTater("grass_tater", GRASS_TATER);
        registerTater("hay_tater", HAY_TATER);
        registerTater("honey_tater", HONEY_TATER);
        registerTater("honeycomb_tater", HONEYCOMB_TATER);
        registerTater("horn_coral_tater", HORN_CORAL_TATER);
        registerTater("jungle_tater", JUNGLE_TATER);
        registerTater("light_tater", LIGHT_TATER);
        registerTater("mycelium_tater", MYCELIUM_TATER);
        registerTater("nether_wart_tater", NETHER_WART_TATER);
        registerTater("oak_tater", OAK_TATER);
        registerTater("obsidian_tater", OBSIDIAN_TATER);
        registerTater("podzol_tater", PODZOL_TATER);
        registerTater("prismarine_brick_tater", PRISMARINE_BRICK_TATER);
        registerTater("prismarine_tater", PRISMARINE_TATER);
        registerTater("red_sand_tater", RED_SAND_TATER);
        registerTater("sand_tater", SAND_TATER);
        registerTater("sea_lantern_tater", SEA_LANTERN_TATER);
        registerTater("shroomlight_tater", SHROOMLIGHT_TATER);
        registerTater("shulker_tater", SHULKER_TATER);
        registerTater("smooth_stone_tater", SMOOTH_STONE_TATER);
        registerTater("soul_sand_tater", SOUL_SAND_TATER);
        registerTater("sponge_tater", SPONGE_TATER);
        registerTater("spruce_tater", SPRUCE_TATER);
        registerTater("stone_brick_tater", STONE_BRICK_TATER);
        registerTater("structure_void_tater", STRUCTURE_VOID_TATER);
        registerTater("target_tater", TARGET_TATER);
        registerTater("terracotta_tater", TERRACOTTA_TATER);
        registerTater("tntater", TNTATER);
        registerTater("tube_coral_tater", TUBE_CORAL_TATER);
        registerTater("warped_tater", WARPED_TATER);
        registerTater("warped_wart_tater", WARPED_WART_TATER);
        registerTater("wool_tater", WOOL_TATER);

        registerTater("acacia_log_tater", ACACIA_LOG_TATER);
        registerTater("angry_bee_tater", ANGRY_BEE_TATER);
        registerTater("beacon_tater", BEACON_TATER);
        registerTater("bee_nest_tater", BEE_NEST_TATER);
        registerTater("bee_tater", BEE_TATER);
        registerTater("beehive_tater", BEEHIVE_TATER);
        registerTater("birch_log_tater", BIRCH_LOG_TATER);
        registerTater("blackstone_tater", BLACKSTONE_TATER);
        registerTater("blaze_tater", BLAZE_TATER);
        registerTater("bookshelf_tater", BOOKSHELF_TATER);
        registerTater("brown_mooshroom_tater", BROWN_MOOSHROOM_TATER);
        registerTater("brown_mushroom_tater", BROWN_MUSHROOM_TATER);
        registerTater("cave_spider_tater", CAVE_SPIDER_TATER);
        registerTater("cobbled_deepslate_tater", COBBLED_DEEPSLATE_TATER);
        registerTater("cobblestone_tater", COBBLESTONE_TATER);
        registerTater("cocoa_tater", COCOA_TATER);
        registerTater("cold_strider_tater", COLD_STRIDER_TATER);
        registerTater("cow_tater", COW_TATER);
        registerTater("crafting_tater", CRAFTING_TATER);
        registerTater("crimson_nylium_tater", CRIMSON_NYLIUM_TATER);
        registerTater("crimson_stem_tater", CRIMSON_STEM_TATER);
        registerTater("dark_oak_log_tater", DARK_OAK_LOG_TATER);
        registerTater("daylight_detector_tater", DAYLIGHT_DETECTOR_TATER);
        registerTater("dead_brain_coral_tater", DEAD_BRAIN_CORAL_TATER);
        registerTater("dead_bubble_coral_tater", DEAD_BUBBLE_CORAL_TATER);
        registerTater("dead_fire_coral_tater", DEAD_FIRE_CORAL_TATER);
        registerTater("dead_horn_coral_tater", DEAD_HORN_CORAL_TATER);
        registerTater("dead_tube_coral_tater", DEAD_TUBE_CORAL_TATER);
        registerTater("deepslate_brick_tater", DEEPSLATE_BRICK_TATER);
        registerTater("deepslate_tater", DEEPSLATE_TATER);
        registerTater("dried_kelp_tater", DRIED_KELP_TATER);
        registerTater("drowned_tater", DROWNED_TATER);
        registerTater("eye_of_ender_tater", EYE_OF_ENDER_TATER);
        registerTater("fox_tater", FOX_TATER);
        registerTater("ghast_tater", GHAST_TATER);
        registerTater("gilded_blackstone_tater", GILDED_BLACKSTONE_TATER);
        registerTater("glow_squid_tater", GLOW_SQUID_TATER);
        registerTater("glowstone_tater", GLOWSTONE_TATER);
        registerTater("husk_tater", HUSK_TATER);
        registerTater("inverted_daylight_detector_tater", INVERTED_DAYLIGHT_DETECTOR_TATER);
        registerTater("jungle_log_tater", JUNGLE_LOG_TATER);
        registerTater("magma_cube_tater", MAGMA_CUBE_TATER);
        registerTater("moobloom_tater", MOOBLOOM_TATER);
        registerTater("moolip_tater", MOOLIP_TATER);
        registerTater("muddy_pig_tater", MUDDY_PIG_TATER);
        registerTater("mushroom_stem_tater", MUSHROOM_STEM_TATER);
        registerTater("nether_brick_tater", NETHER_BRICK_TATER);
        registerTater("netherrack_tater", NETHERRACK_TATER);
        registerTater("oak_log_tater", OAK_LOG_TATER);
        registerTater("pig_tater", PIG_TATER);
        registerTater("polar_bear_tater", POLAR_BEAR_TATER);
        registerTater("puffertater", PUFFERTATER);
        registerTater("red_mooshroom_tater", RED_MOOSHROOM_TATER);
        registerTater("red_mushroom_tater", RED_MUSHROOM_TATER);
        registerTater("red_nether_brick_tater", RED_NETHER_BRICK_TATER);
        registerTater("red_sandstone_tater", RED_SANDSTONE_TATER);
        registerTater("ruby_tater", RUBY_TATER);
        registerTater("sandstone_tater", SANDSTONE_TATER);
        registerTater("sea_pickle_tater", SEA_PICKLE_TATER);
        registerTater("sheep_tater", SHEEP_TATER);
        registerTater("shielded_wither_tater", SHIELDED_WITHER_TATER);
        registerTater("snow_fox_tater", SNOW_FOX_TATER);
        registerTater("spider_tater", SPIDER_TATER);
        registerTater("spruce_log_tater", SPRUCE_LOG_TATER);
        registerTater("squid_tater", SQUID_TATER);
        registerTater("stray_tater", STRAY_TATER);
        registerTater("strider_tater", STRIDER_TATER);
        registerTater("turtle_egg_tater", TURTLE_EGG_TATER);
        registerTater("warped_nylium_tater", WARPED_NYLIUM_TATER);
        registerTater("warped_stem_tater", WARPED_STEM_TATER);
        registerTater("wither_tater", WITHER_TATER);

        registerTater("azalea_tater", AZALEA_TATER);
        registerTater("bell_tater", BELL_TATER);
        registerTater("cold_frog_tater", COLD_FROG_TATER);
        registerTater("conduit_tater", CONDUIT_TATER);
        registerTater("elder_guardian_tater", ELDER_GUARDIAN_TATER);
        registerTater("end_stone_brick_tater", END_STONE_BRICK_TATER);
        registerTater("flower_pot_tater", FLOWER_POT_TATER);
        registerTater("guardian_tater", GUARDIAN_TATER);
        registerTater("illager_tater", ILLAGER_TATER);
        registerTater("illusioner_tater", ILLUSIONER_TATER);
        registerTater("jukebox_tater", JUKEBOX_TATER);
        registerTater("lantern_tater", LANTERN_TATER);
        registerTater("piglin_tater", PIGLIN_TATER);
        registerTater("pink_wither_tater", PINK_WITHER_TATER);
        registerTater("piston_tater", PISTON_TATER);
        registerTater("purpur_tater", PURPUR_TATER);
        registerTater("soul_lantern_tater", SOUL_LANTERN_TATER);
        registerTater("soul_soil_tater", SOUL_SOIL_TATER);
        registerTater("sticky_piston_tater", STICKY_PISTON_TATER);
        registerTater("temperate_frog_tater", TEMPERATE_FROG_TATER);
        registerTater("underwater_tntater", UNDERWATER_TNTATER);
        registerTater("vex_tater", VEX_TATER);
        registerTater("villager_tater", VILLAGER_TATER);
        registerTater("vinditater", VINDITATER);
        registerTater("wandering_trader_tater", WANDERING_TRADER_TATER);
        registerTater("warm_frog_tater", WARM_FROG_TATER);
        registerTater("wax_tater", WAX_TATER);
        registerTater("witch_tater", WITCH_TATER);
        registerTater("zombie_villager_tater", ZOMBIE_VILLAGER_TATER);
        registerTater("zombified_piglin_tater", ZOMBIFIED_PIGLIN_TATER);

        registerTater("bone_spider_tater", BONE_SPIDER_TATER);
        registerTater("bouldering_zombie_tater", BOULDERING_ZOMBIE_TATER);
        registerTater("charged_creeper_tater", CHARGED_CREEPER_TATER);
        registerTater("lobber_zombie_tater", LOBBER_ZOMBIE_TATER);
        registerTater("mossy_skeletater", MOSSY_SKELETATER);
        registerTater("stripped_acacia_log_tater", STRIPPED_ACACIA_LOG_TATER);
        registerTater("stripped_birch_log_tater", STRIPPED_BIRCH_LOG_TATER);
        registerTater("stripped_crimson_stem_tater", STRIPPED_CRIMSON_STEM_TATER);
        registerTater("stripped_dark_oak_log_tater", STRIPPED_DARK_OAK_LOG_TATER);
        registerTater("stripped_jungle_log_tater", STRIPPED_JUNGLE_LOG_TATER);
        registerTater("stripped_oak_log_tater", STRIPPED_OAK_LOG_TATER);
        registerTater("stripped_spruce_log_tater", STRIPPED_SPRUCE_LOG_TATER);
        registerTater("stripped_warped_stem_tater", STRIPPED_WARPED_STEM_TATER);
        registerTater("tropical_slime_tater", TROPICAL_SLIME_TATER);

        registerTater("apple_tater", APPLE_TATER);
        registerTater("golden_apple_tater", GOLDEN_APPLE_TATER);
        registerTater("ice_tater", ICE_TATER);
        registerTater("king_tater", KING_TATER);
        registerTater("raw_copper_tater", RAW_COPPER_TATER);
        registerTater("raw_gold_tater", RAW_GOLD_TATER);
        registerTater("raw_iron_tater", RAW_IRON_TATER);

        registerTater("allay_tater", ALLAY_TATER);
        registerTater("mangrove_log_tater", MANGROVE_LOG_TATER);
        registerTater("mangrove_tater", MANGROVE_TATER);
        registerTater("mud_brick_tater", MUD_BRICK_TATER);
        registerTater("mud_tater", MUD_TATER);
        registerTater("packed_mud_tater", PACKED_MUD_TATER);
        registerTater("stripped_mangrove_log_tater", STRIPPED_MANGROVE_LOG_TATER);

        registerTater("lucy_axolotl_tater", LUCY_AXOLOTL_TATER);
        registerTater("wild_axolotl_tater", WILD_AXOLOTL_TATER);
        registerTater("gold_axolotl_tater", GOLD_AXOLOTL_TATER);
        registerTater("cyan_axolotl_tater", CYAN_AXOLOTL_TATER);
        registerTater("blue_axolotl_tater", BLUE_AXOLOTL_TATER);
        
        registerTater("bronze_capsule_tater", BRONZE_CAPSULE_TATER);
        registerTater("silver_capsule_tater", SILVER_CAPSULE_TATER);
        registerTater("gold_capsule_tater", GOLD_CAPSULE_TATER);

        registerTater("corruptater", CORRUPTATER);

        register("tater_box", TATER_BOX);
        register("creative_tater_box", CREATIVE_TATER_BOX);

        register("quick_armor_stand", QUICK_ARMOR_STAND);
        register("game_portal_opener", GAME_PORTAL_OPENER);
        register("launch_feather", LAUNCH_FEATHER);

        PolymerItemGroupUtils.registerPolymerItemGroup(NucleoidExtras.identifier("general"), ITEM_GROUP);

        ServerPlayConnectionEvents.JOIN.register(NEItems::onPlayerJoin);

        UseBlockCallback.EVENT.register(NEItems::onUseBlock);
        UseEntityCallback.EVENT.register(NEItems::onUseEntity);
    }

    private static boolean tryOfferStack(ServerPlayerEntity player, Item item, Consumer<ItemStack> consumer) {
        var inventory = player.getInventory();

        if (inventory.containsAny(Collections.singleton(item))) {
            return false;
        }

        var stack = new ItemStack(item);
        consumer.accept(stack);

        player.getInventory().offer(stack, true);
        return true;
    }

    private static boolean tryOfferStack(ServerPlayerEntity player, Item item) {
        return tryOfferStack(player, item, stack -> {});
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer server) {
        giveLobbyItems(handler.getPlayer());
    }

    public static void giveLobbyItems(ServerPlayerEntity player) {
        var config = NucleoidExtrasConfig.get();

        tryOfferStack(player, TATER_BOX);

        config.gamePortalOpener().ifPresent(gamePortal -> {
            tryOfferStack(player, GAME_PORTAL_OPENER, stack -> {
                GamePortalOpenerItem.setGamePortalId(stack, gamePortal);
            });
        });
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!player.getWorld().isClient() && hitResult != null && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();

            PlayerLobbyState state = PlayerLobbyState.get(player);
            state.collectTaterFromBlock(world, pos, stack, player);
        }

        return ActionResult.PASS;
    }

    private static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (!player.getWorld().isClient() && hitResult != null) {
            ItemStack stack = player.getStackInHand(hand);
            Vec3d hitPos = hitResult.getPos().subtract(entity.getPos());

            PlayerLobbyState state = PlayerLobbyState.get(player);
            state.collectTaterFromEntity(entity, hitPos, stack, player);
        }

        return ActionResult.PASS;
    }

    private static <T extends Item> T registerTater(String id, T item) {
        register(id, item);
        TATERS.add(item);
        return item;
    }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registries.ITEM, NucleoidExtras.identifier(id), item);
    }
}
