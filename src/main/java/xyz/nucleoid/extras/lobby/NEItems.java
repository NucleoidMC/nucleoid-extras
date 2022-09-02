package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.api.block.PolymerHeadBlock;
import eu.pb4.polymer.api.item.PolymerItemGroup;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.lobby.item.*;

import java.util.Collections;
import java.util.function.Consumer;

public class NEItems {
    public static final PolymerItemGroup ITEM_GROUP = PolymerItemGroup.create(NucleoidExtras.identifier("general"), Text.translatable("text.nucleoid_extras.name"));

    public static final Item NUCLEOID_LOGO = createHead(NEBlocks.NUCLEOID_LOGO);

    public static final Item END_PORTAL = createSimple(NEBlocks.END_PORTAL, Items.BLACK_CARPET);
    public static final Item END_GATEWAY = createSimple(NEBlocks.END_GATEWAY, Items.BLACK_WOOL);
    public static final Item SAFE_TNT = createSimple(NEBlocks.SAFE_TNT, Items.TNT);

    public static final Item GOLD_LAUNCH_PAD = createSimple(NEBlocks.GOLD_LAUNCH_PAD, Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Item IRON_LAUNCH_PAD = createSimple(NEBlocks.IRON_LAUNCH_PAD, Items.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final Item INFINITE_DISPENSER = createSimple(NEBlocks.INFINITE_DISPENSER, Items.DISPENSER);
    public static final Item INFINITE_DROPPER = createSimple(NEBlocks.INFINITE_DROPPER, Items.DROPPER);
    public static final Item SNAKE_BLOCK = createSimple(NEBlocks.SNAKE_BLOCK, Items.LIME_CONCRETE);
    public static final Item FAST_SNAKE_BLOCK = createSimple(NEBlocks.FAST_SNAKE_BLOCK, Items.LIGHT_BLUE_CONCRETE);

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

    public static final Item BRONZE_CAPSULE_TATER = createHead(NEBlocks.BRONZE_CAPSULE_TATER);
    public static final Item SILVER_CAPSULE_TATER = createHead(NEBlocks.SILVER_CAPSULE_TATER);
    public static final Item GOLD_CAPSULE_TATER = createHead(NEBlocks.GOLD_CAPSULE_TATER);

    public static final Item CORRUPTATER = createHead(NEBlocks.CORRUPTATER);

    public static final Item TATER_BOX = new TaterBoxItem(new Item.Settings().group(ITEM_GROUP).maxDamage(0));
    public static final Item QUICK_ARMOR_STAND = new QuickArmorStandItem(new Item.Settings().group(ITEM_GROUP));
    public static final Item GAME_PORTAL_OPENER = new GamePortalOpenerItem(new Item.Settings().group(ITEM_GROUP).maxCount(1));

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
        register("snake_block", SNAKE_BLOCK);
        register("fast_snake_block", FAST_SNAKE_BLOCK);

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
        register("flowering_azalea_tater", FLOWERING_AZALEA_TATER);
        register("stone_tater", STONE_TATER);
        register("calcite_tater", CALCITE_TATER);
        register("tuff_tater", TUFF_TATER);
        register("basalt_tater", BASALT_TATER);
        register("dripstone_tater", DRIPSTONE_TATER);
        register("amethyst_tater", AMETHYST_TATER);
        register("packed_ice_tater", PACKED_ICE_TATER);
        register("blue_ice_tater", BLUE_ICE_TATER);
        register("flame_tater", FLAME_TATER);

        register("puzzle_cube_tater", PUZZLE_CUBE_TATER);
        register("lucky_tater", LUCKY_TATER);
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
        register("viral_tater", VIRAL_TATER);
        register("tateroid", TATEROID);
        register("red_tateroid", RED_TATEROID);
        register("orange_tateroid", ORANGE_TATEROID);
        register("yellow_tateroid", YELLOW_TATEROID);
        register("green_tateroid", GREEN_TATEROID);
        register("blue_tateroid", BLUE_TATEROID);
        register("purple_tateroid", PURPLE_TATEROID);
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

        register("coal_tater", COAL_TATER);
        register("diamond_tater", DIAMOND_TATER);
        register("emerald_tater", EMERALD_TATER);
        register("gold_tater", GOLD_TATER);
        register("iron_tater", IRON_TATER);
        register("lapis_tater", LAPIS_TATER);
        register("netherite_tater", NETHERITE_TATER);
        register("quartz_tater", QUARTZ_TATER);
        register("redstone_tater", REDSTONE_TATER);

        register("copper_tater", COPPER_TATER);
        register("exposed_copper_tater", EXPOSED_COPPER_TATER);
        register("weathered_copper_tater", WEATHERED_COPPER_TATER);
        register("oxidized_copper_tater", OXIDIZED_COPPER_TATER);

        register("cake_tater", CAKE_TATER);
        register("endertater", ENDERTATER);
        register("furnace_tater", FURNACE_TATER);
        register("melon_tater", MELON_TATER);
        register("pumpkin_tater", PUMPKIN_TATER);
        register("jack_o_tater", JACK_O_TATER);
        register("sculk_tater", SCULK_TATER);
        register("slime_tater", SLIME_TATER);
        register("herobrine_tater", HEROBRINE_TATER);
        register("ochre_froglight_tater", OCHRE_FROGLIGHT_TATER);
        register("pearlescent_froglight_tater", PEARLESCENT_FROGLIGHT_TATER);
        register("verdant_froglight_tater", VERDANT_FROGLIGHT_TATER);
        register("snowman_tater", SNOWMAN_TATER);

        register("acacia_tater", ACACIA_TATER);
        register("andesite_tater", ANDESITE_TATER);
        register("bamboo_tater", BAMBOO_TATER);
        register("bedrock_tater", BEDROCK_TATER);
        register("birch_tater", BIRCH_TATER);
        register("bone_tater", BONE_TATER);
        register("brain_coral_tater", BRAIN_CORAL_TATER);
        register("brick_tater", BRICK_TATER);
        register("bubble_coral_tater", BUBBLE_CORAL_TATER);
        register("cactus_tater", CACTUS_TATER);
        register("chorus_tater", CHORUS_TATER);
        register("clay_tater", CLAY_TATER);
        register("crimson_tater", CRIMSON_TATER);
        register("dark_oak_tater", DARK_OAK_TATER);
        register("dark_prismarine_tater", DARK_PRISMARINE_TATER);
        register("diorite_tater", DIORITE_TATER);
        register("dirt_tater", DIRT_TATER);
        register("end_stone_tater", END_STONE_TATER);
        register("fire_coral_tater", FIRE_CORAL_TATER);
        register("granite_tater", GRANITE_TATER);
        register("grass_tater", GRASS_TATER);
        register("hay_tater", HAY_TATER);
        register("honey_tater", HONEY_TATER);
        register("honeycomb_tater", HONEYCOMB_TATER);
        register("horn_coral_tater", HORN_CORAL_TATER);
        register("jungle_tater", JUNGLE_TATER);
        register("mycelium_tater", MYCELIUM_TATER);
        register("nether_wart_tater", NETHER_WART_TATER);
        register("oak_tater", OAK_TATER);
        register("obsidian_tater", OBSIDIAN_TATER);
        register("podzol_tater", PODZOL_TATER);
        register("prismarine_brick_tater", PRISMARINE_BRICK_TATER);
        register("prismarine_tater", PRISMARINE_TATER);
        register("red_sand_tater", RED_SAND_TATER);
        register("sand_tater", SAND_TATER);
        register("sea_lantern_tater", SEA_LANTERN_TATER);
        register("shroomlight_tater", SHROOMLIGHT_TATER);
        register("shulker_tater", SHULKER_TATER);
        register("smooth_stone_tater", SMOOTH_STONE_TATER);
        register("soul_sand_tater", SOUL_SAND_TATER);
        register("sponge_tater", SPONGE_TATER);
        register("spruce_tater", SPRUCE_TATER);
        register("stone_brick_tater", STONE_BRICK_TATER);
        register("target_tater", TARGET_TATER);
        register("terracotta_tater", TERRACOTTA_TATER);
        register("tntater", TNTATER);
        register("tube_coral_tater", TUBE_CORAL_TATER);
        register("warped_tater", WARPED_TATER);
        register("warped_wart_tater", WARPED_WART_TATER);
        register("wool_tater", WOOL_TATER);

        register("acacia_log_tater", ACACIA_LOG_TATER);
        register("angry_bee_tater", ANGRY_BEE_TATER);
        register("beacon_tater", BEACON_TATER);
        register("bee_nest_tater", BEE_NEST_TATER);
        register("bee_tater", BEE_TATER);
        register("beehive_tater", BEEHIVE_TATER);
        register("birch_log_tater", BIRCH_LOG_TATER);
        register("blackstone_tater", BLACKSTONE_TATER);
        register("blaze_tater", BLAZE_TATER);
        register("bookshelf_tater", BOOKSHELF_TATER);
        register("brown_mooshroom_tater", BROWN_MOOSHROOM_TATER);
        register("brown_mushroom_tater", BROWN_MUSHROOM_TATER);
        register("cave_spider_tater", CAVE_SPIDER_TATER);
        register("cobbled_deepslate_tater", COBBLED_DEEPSLATE_TATER);
        register("cobblestone_tater", COBBLESTONE_TATER);
        register("cocoa_tater", COCOA_TATER);
        register("cold_strider_tater", COLD_STRIDER_TATER);
        register("cow_tater", COW_TATER);
        register("crafting_tater", CRAFTING_TATER);
        register("crimson_nylium_tater", CRIMSON_NYLIUM_TATER);
        register("crimson_stem_tater", CRIMSON_STEM_TATER);
        register("dark_oak_log_tater", DARK_OAK_LOG_TATER);
        register("daylight_detector_tater", DAYLIGHT_DETECTOR_TATER);
        register("dead_brain_coral_tater", DEAD_BRAIN_CORAL_TATER);
        register("dead_bubble_coral_tater", DEAD_BUBBLE_CORAL_TATER);
        register("dead_fire_coral_tater", DEAD_FIRE_CORAL_TATER);
        register("dead_horn_coral_tater", DEAD_HORN_CORAL_TATER);
        register("dead_tube_coral_tater", DEAD_TUBE_CORAL_TATER);
        register("deepslate_brick_tater", DEEPSLATE_BRICK_TATER);
        register("deepslate_tater", DEEPSLATE_TATER);
        register("dried_kelp_tater", DRIED_KELP_TATER);
        register("drowned_tater", DROWNED_TATER);
        register("eye_of_ender_tater", EYE_OF_ENDER_TATER);
        register("fox_tater", FOX_TATER);
        register("ghast_tater", GHAST_TATER);
        register("gilded_blackstone_tater", GILDED_BLACKSTONE_TATER);
        register("glow_squid_tater", GLOW_SQUID_TATER);
        register("glowstone_tater", GLOWSTONE_TATER);
        register("husk_tater", HUSK_TATER);
        register("inverted_daylight_detector_tater", INVERTED_DAYLIGHT_DETECTOR_TATER);
        register("jungle_log_tater", JUNGLE_LOG_TATER);
        register("magma_cube_tater", MAGMA_CUBE_TATER);
        register("moobloom_tater", MOOBLOOM_TATER);
        register("muddy_pig_tater", MUDDY_PIG_TATER);
        register("mushroom_stem_tater", MUSHROOM_STEM_TATER);
        register("nether_brick_tater", NETHER_BRICK_TATER);
        register("netherrack_tater", NETHERRACK_TATER);
        register("oak_log_tater", OAK_LOG_TATER);
        register("pig_tater", PIG_TATER);
        register("polar_bear_tater", POLAR_BEAR_TATER);
        register("puffertater", PUFFERTATER);
        register("red_mooshroom_tater", RED_MOOSHROOM_TATER);
        register("red_mushroom_tater", RED_MUSHROOM_TATER);
        register("red_nether_brick_tater", RED_NETHER_BRICK_TATER);
        register("red_sandstone_tater", RED_SANDSTONE_TATER);
        register("ruby_tater", RUBY_TATER);
        register("sandstone_tater", SANDSTONE_TATER);
        register("sea_pickle_tater", SEA_PICKLE_TATER);
        register("sheep_tater", SHEEP_TATER);
        register("shielded_wither_tater", SHIELDED_WITHER_TATER);
        register("snow_fox_tater", SNOW_FOX_TATER);
        register("spider_tater", SPIDER_TATER);
        register("spruce_log_tater", SPRUCE_LOG_TATER);
        register("squid_tater", SQUID_TATER);
        register("stray_tater", STRAY_TATER);
        register("strider_tater", STRIDER_TATER);
        register("turtle_egg_tater", TURTLE_EGG_TATER);
        register("warped_nylium_tater", WARPED_NYLIUM_TATER);
        register("warped_stem_tater", WARPED_STEM_TATER);
        register("wither_tater", WITHER_TATER);

        register("azalea_tater", AZALEA_TATER);
        register("bell_tater", BELL_TATER);
        register("cold_frog_tater", COLD_FROG_TATER);
        register("conduit_tater", CONDUIT_TATER);
        register("elder_guardian_tater", ELDER_GUARDIAN_TATER);
        register("end_stone_brick_tater", END_STONE_BRICK_TATER);
        register("flower_pot_tater", FLOWER_POT_TATER);
        register("guardian_tater", GUARDIAN_TATER);
        register("illager_tater", ILLAGER_TATER);
        register("illusioner_tater", ILLUSIONER_TATER);
        register("jukebox_tater", JUKEBOX_TATER);
        register("lantern_tater", LANTERN_TATER);
        register("piglin_tater", PIGLIN_TATER);
        register("pink_wither_tater", PINK_WITHER_TATER);
        register("piston_tater", PISTON_TATER);
        register("purpur_tater", PURPUR_TATER);
        register("soul_lantern_tater", SOUL_LANTERN_TATER);
        register("soul_soil_tater", SOUL_SOIL_TATER);
        register("sticky_piston_tater", STICKY_PISTON_TATER);
        register("temperate_frog_tater", TEMPERATE_FROG_TATER);
        register("underwater_tntater", UNDERWATER_TNTATER);
        register("vex_tater", VEX_TATER);
        register("villager_tater", VILLAGER_TATER);
        register("vinditater", VINDITATER);
        register("wandering_trader_tater", WANDERING_TRADER_TATER);
        register("warm_frog_tater", WARM_FROG_TATER);
        register("wax_tater", WAX_TATER);
        register("witch_tater", WITCH_TATER);
        register("zombie_villager_tater", ZOMBIE_VILLAGER_TATER);
        register("zombified_piglin_tater", ZOMBIFIED_PIGLIN_TATER);

        register("bone_spider_tater", BONE_SPIDER_TATER);
        register("bouldering_zombie_tater", BOULDERING_ZOMBIE_TATER);
        register("charged_creeper_tater", CHARGED_CREEPER_TATER);
        register("lobber_zombie_tater", LOBBER_ZOMBIE_TATER);
        register("mossy_skeletater", MOSSY_SKELETATER);
        register("stripped_acacia_log_tater", STRIPPED_ACACIA_LOG_TATER);
        register("stripped_birch_log_tater", STRIPPED_BIRCH_LOG_TATER);
        register("stripped_crimson_stem_tater", STRIPPED_CRIMSON_STEM_TATER);
        register("stripped_dark_oak_log_tater", STRIPPED_DARK_OAK_LOG_TATER);
        register("stripped_jungle_log_tater", STRIPPED_JUNGLE_LOG_TATER);
        register("stripped_oak_log_tater", STRIPPED_OAK_LOG_TATER);
        register("stripped_spruce_log_tater", STRIPPED_SPRUCE_LOG_TATER);
        register("stripped_warped_stem_tater", STRIPPED_WARPED_STEM_TATER);
        register("tropical_slime_tater", TROPICAL_SLIME_TATER);

        register("apple_tater", APPLE_TATER);
        register("golden_apple_tater", GOLDEN_APPLE_TATER);
        register("ice_tater", ICE_TATER);
        register("king_tater", KING_TATER);
        register("raw_copper_tater", RAW_COPPER_TATER);
        register("raw_gold_tater", RAW_GOLD_TATER);
        register("raw_iron_tater", RAW_IRON_TATER);

        register("allay_tater", ALLAY_TATER);
        register("mangrove_log_tater", MANGROVE_LOG_TATER);
        register("mangrove_tater", MANGROVE_TATER);
        register("mud_brick_tater", MUD_BRICK_TATER);
        register("mud_tater", MUD_TATER);
        register("packed_mud_tater", PACKED_MUD_TATER);
        register("stripped_mangrove_log_tater", STRIPPED_MANGROVE_LOG_TATER);

        register("bronze_capsule_tater", BRONZE_CAPSULE_TATER);
        register("silver_capsule_tater", SILVER_CAPSULE_TATER);
        register("gold_capsule_tater", GOLD_CAPSULE_TATER);

        register("corruptater", CORRUPTATER);

        register("tater_box", TATER_BOX);
        register("quick_armor_stand", QUICK_ARMOR_STAND);
        register("game_portal_opener", GAME_PORTAL_OPENER);

        ServerPlayConnectionEvents.JOIN.register(NEItems::onPlayerJoin);

        UseEntityCallback.EVENT.register(NEItems::onUseEntity);

        ITEM_GROUP.setIcon(NUCLEOID_LOGO.getDefaultStack());
    }

    private static boolean tryOfferStack(ServerPlayNetworkHandler handler, Item item, Consumer<ItemStack> consumer) {
        var inventory = handler.getPlayer().getInventory();

        if (inventory.containsAny(Collections.singleton(item))) {
            return false;
        }

        var stack = new ItemStack(item);
        consumer.accept(stack);

        handler.getPlayer().getInventory().offer(stack, true);
        return true;
    }

    private static boolean tryOfferStack(ServerPlayNetworkHandler handler, Item item) {
        return tryOfferStack(handler, item, stack -> {});
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer server) {
        var config = NucleoidExtrasConfig.get();

        tryOfferStack(handler, TATER_BOX);

        config.gamePortalOpener().ifPresent(gamePortal -> {
            tryOfferStack(handler, GAME_PORTAL_OPENER, stack -> {
                GamePortalOpenerItem.setGamePortalId(stack, gamePortal);
            });
        });
    }

    private static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (!player.getWorld().isClient() && hitResult != null) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof TaterBoxItem taterBox) {
                Vec3d hitPos = hitResult.getPos().subtract(entity.getPos());
                ActionResult result = taterBox.tryAdd(entity, hitPos, stack, player);

                return result.isAccepted() ? result : ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    private static <T extends Item> T register(String id, T item) {
        return Registry.register(Registry.ITEM, NucleoidExtras.identifier(id), item);
    }
}
