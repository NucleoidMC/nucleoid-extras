package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.component.GamePortalComponent;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterPositionsComponent;
import xyz.nucleoid.extras.component.TaterSelectionComponent;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.item.GamePortalOpenerItem;
import xyz.nucleoid.extras.lobby.item.LaunchFeatherItem;
import xyz.nucleoid.extras.lobby.item.LobbyBlockItem;
import xyz.nucleoid.extras.lobby.item.LobbyHeadItem;
import xyz.nucleoid.extras.lobby.item.LobbyTallBlockItem;
import xyz.nucleoid.extras.lobby.item.QuickArmorStandItem;
import xyz.nucleoid.extras.lobby.item.RuleBookItem;
import xyz.nucleoid.extras.lobby.item.tater.CreativeTaterBoxItem;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.lobby.item.tater.TaterGuidebookItem;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
            entries.add(NEItems.TRANSIENT_PALE_OAK_DOOR);
            entries.add(NEItems.TRANSIENT_BAMBOO_DOOR);
            entries.add(NEItems.TRANSIENT_CRIMSON_DOOR);
            entries.add(NEItems.TRANSIENT_WARPED_DOOR);
            entries.add(NEItems.TRANSIENT_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_EXPOSED_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_WEATHERED_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_OXIDIZED_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_WAXED_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR);
            entries.add(NEItems.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR);
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
            entries.add(NEItems.TATER_GUIDEBOOK);
            TATERS.forEach(entries::add);
        })
        .build();

    public static final Item NUCLEOID_LOGO = registerHead("nucleoid_logo", NEBlocks.NUCLEOID_LOGO);
    public static final Item NUCLE_PAST_LOGO = registerHead("nucle_past_logo", NEBlocks.NUCLE_PAST_LOGO);

    public static final Item END_PORTAL = registerSimple("end_portal", NEBlocks.END_PORTAL, Items.BLACK_CARPET);
    public static final Item END_GATEWAY = registerSimple("end_gateway", NEBlocks.END_GATEWAY, Items.BLACK_WOOL);
    public static final Item SAFE_TNT = registerSimple("safe_tnt", NEBlocks.SAFE_TNT, Items.TNT);

    public static final Item GOLD_LAUNCH_PAD = registerSimple("gold_launch_pad", NEBlocks.GOLD_LAUNCH_PAD, Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Item IRON_LAUNCH_PAD = registerSimple("iron_launch_pad", NEBlocks.IRON_LAUNCH_PAD, Items.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final Item CONTRIBUTOR_STATUE = registerSimple("contributor_statue", NEBlocks.CONTRIBUTOR_STATUE, Items.SMOOTH_STONE);

    public static final Item INFINITE_DISPENSER = registerSimple("infinite_dispenser", NEBlocks.INFINITE_DISPENSER, Items.DISPENSER);
    public static final Item INFINITE_DROPPER = registerSimple("infinite_dropper", NEBlocks.INFINITE_DROPPER, Items.DROPPER);
    public static final Item SNAKE_BLOCK = registerSimple("snake_block", NEBlocks.SNAKE_BLOCK, Items.LIME_CONCRETE);
    public static final Item FAST_SNAKE_BLOCK = registerSimple("fast_snake_block", NEBlocks.FAST_SNAKE_BLOCK, Items.LIGHT_BLUE_CONCRETE);

    public static final Item TRANSIENT_IRON_DOOR = register("transient_iron_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_IRON_DOOR, settings, Items.IRON_DOOR));
    public static final Item TRANSIENT_OAK_DOOR = register("transient_oak_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_OAK_DOOR, settings, Items.OAK_DOOR));
    public static final Item TRANSIENT_SPRUCE_DOOR = register("transient_spruce_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_SPRUCE_DOOR, settings, Items.SPRUCE_DOOR));
    public static final Item TRANSIENT_BIRCH_DOOR = register("transient_birch_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_BIRCH_DOOR, settings, Items.BIRCH_DOOR));
    public static final Item TRANSIENT_JUNGLE_DOOR = register("transient_jungle_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_JUNGLE_DOOR, settings, Items.JUNGLE_DOOR));
    public static final Item TRANSIENT_ACACIA_DOOR = register("transient_acacia_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_ACACIA_DOOR, settings, Items.ACACIA_DOOR));
    public static final Item TRANSIENT_CHERRY_DOOR = register("transient_cherry_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_CHERRY_DOOR, settings, Items.CHERRY_DOOR));
    public static final Item TRANSIENT_DARK_OAK_DOOR = register("transient_dark_oak_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_DARK_OAK_DOOR, settings, Items.DARK_OAK_DOOR));
    public static final Item TRANSIENT_MANGROVE_DOOR = register("transient_mangrove_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_MANGROVE_DOOR, settings, Items.MANGROVE_DOOR));
    public static final Item TRANSIENT_PALE_OAK_DOOR = register("transient_pale_oak_door", new Item.Settings().useBlockPrefixedTranslationKey().requires(FeatureFlags.WINTER_DROP), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_PALE_OAK_DOOR, settings, Items.PALE_OAK_DOOR));
    public static final Item TRANSIENT_BAMBOO_DOOR = register("transient_bamboo_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_BAMBOO_DOOR, settings, Items.BAMBOO_DOOR));
    public static final Item TRANSIENT_CRIMSON_DOOR = register("transient_crimson_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_CRIMSON_DOOR, settings, Items.CRIMSON_DOOR));
    public static final Item TRANSIENT_WARPED_DOOR = register("transient_warped_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_WARPED_DOOR, settings, Items.WARPED_DOOR));
    public static final Item TRANSIENT_COPPER_DOOR = register("transient_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_COPPER_DOOR, settings, Items.COPPER_DOOR));
    public static final Item TRANSIENT_EXPOSED_COPPER_DOOR = register("transient_exposed_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR, settings, Items.EXPOSED_COPPER_DOOR));
    public static final Item TRANSIENT_WEATHERED_COPPER_DOOR = register("transient_weathered_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR, settings, Items.WEATHERED_COPPER_DOOR));
    public static final Item TRANSIENT_OXIDIZED_COPPER_DOOR = register("transient_oxidized_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR, settings, Items.OXIDIZED_COPPER_DOOR));
    public static final Item TRANSIENT_WAXED_COPPER_DOOR = register("transient_waxed_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_WAXED_COPPER_DOOR, settings, Items.WAXED_COPPER_DOOR));
    public static final Item TRANSIENT_WAXED_EXPOSED_COPPER_DOOR = register("transient_waxed_exposed_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR, settings, Items.WAXED_EXPOSED_COPPER_DOOR));
    public static final Item TRANSIENT_WAXED_WEATHERED_COPPER_DOOR = register("transient_waxed_weathered_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR, settings, Items.WAXED_WEATHERED_COPPER_DOOR));
    public static final Item TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR = register("transient_waxed_oxidized_copper_door", new Item.Settings().useBlockPrefixedTranslationKey(), settings -> new LobbyTallBlockItem(NEBlocks.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR, settings, Items.WAXED_OXIDIZED_COPPER_DOOR));

    public static final Item BLACK_CONCRETE_POWDER = registerSimple("black_concrete_powder", NEBlocks.BLACK_CONCRETE_POWDER, Items.BLACK_CONCRETE_POWDER);
    public static final Item BLUE_CONCRETE_POWDER = registerSimple("blue_concrete_powder", NEBlocks.BLUE_CONCRETE_POWDER, Items.BLUE_CONCRETE_POWDER);
    public static final Item BROWN_CONCRETE_POWDER = registerSimple("brown_concrete_powder", NEBlocks.BROWN_CONCRETE_POWDER, Items.BROWN_CONCRETE_POWDER);
    public static final Item CYAN_CONCRETE_POWDER = registerSimple("cyan_concrete_powder", NEBlocks.CYAN_CONCRETE_POWDER, Items.CYAN_CONCRETE_POWDER);
    public static final Item GREEN_CONCRETE_POWDER = registerSimple("green_concrete_powder", NEBlocks.GREEN_CONCRETE_POWDER, Items.GREEN_CONCRETE_POWDER);
    public static final Item GRAY_CONCRETE_POWDER = registerSimple("gray_concrete_powder", NEBlocks.GRAY_CONCRETE_POWDER, Items.GRAY_CONCRETE_POWDER);
    public static final Item LIGHT_BLUE_CONCRETE_POWDER = registerSimple("light_blue_concrete_powder", NEBlocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Item LIGHT_GRAY_CONCRETE_POWDER = registerSimple("light_gray_concrete_powder", NEBlocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Item LIME_CONCRETE_POWDER = registerSimple("lime_concrete_powder", NEBlocks.LIME_CONCRETE_POWDER, Items.LIME_CONCRETE_POWDER);
    public static final Item MAGENTA_CONCRETE_POWDER = registerSimple("magenta_concrete_powder", NEBlocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_CONCRETE_POWDER);
    public static final Item ORANGE_CONCRETE_POWDER = registerSimple("orange_concrete_powder", NEBlocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_CONCRETE_POWDER);
    public static final Item PINK_CONCRETE_POWDER = registerSimple("pink_concrete_powder", NEBlocks.PINK_CONCRETE_POWDER, Items.PINK_CONCRETE_POWDER);
    public static final Item PURPLE_CONCRETE_POWDER = registerSimple("purple_concrete_powder", NEBlocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_CONCRETE_POWDER);
    public static final Item RED_CONCRETE_POWDER = registerSimple("red_concrete_powder", NEBlocks.RED_CONCRETE_POWDER, Items.RED_CONCRETE_POWDER);
    public static final Item WHITE_CONCRETE_POWDER = registerSimple("white_concrete_powder", NEBlocks.WHITE_CONCRETE_POWDER, Items.WHITE_CONCRETE_POWDER);
    public static final Item YELLOW_CONCRETE_POWDER = registerSimple("yellow_concrete_powder", NEBlocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_CONCRETE_POWDER);

    public static final Item TINY_POTATO = registerHead("tiny_potato", NEBlocks.TINY_POTATO);
    public static final Item BOTANICAL_TINY_POTATO = registerHead("botanical_potato", NEBlocks.BOTANICAL_TINY_POTATO);
    public static final Item IRRITATER = registerHead("irritater", NEBlocks.IRRITATER);
    public static final Item SAD_TATER = registerHead("sad_tater", NEBlocks.SAD_TATER);
    public static final Item FLOWERING_AZALEA_TATER = registerHead("flowering_azalea_tater", NEBlocks.FLOWERING_AZALEA_TATER);
    public static final Item STONE_TATER = registerHead("stone_tater", NEBlocks.STONE_TATER);
    public static final Item CALCITE_TATER = registerHead("calcite_tater", NEBlocks.CALCITE_TATER);
    public static final Item TUFF_TATER = registerHead("tuff_tater", NEBlocks.TUFF_TATER);
    public static final Item BASALT_TATER = registerHead("basalt_tater", NEBlocks.BASALT_TATER);
    public static final Item DRIPSTONE_TATER = registerHead("dripstone_tater", NEBlocks.DRIPSTONE_TATER);
    public static final Item AMETHYST_TATER = registerHead("amethyst_tater", NEBlocks.AMETHYST_TATER);
    public static final Item PACKED_ICE_TATER = registerHead("packed_ice_tater", NEBlocks.PACKED_ICE_TATER);
    public static final Item BLUE_ICE_TATER = registerHead("blue_ice_tater", NEBlocks.BLUE_ICE_TATER);
    public static final Item FLAME_TATER = registerHead("flame_tater", NEBlocks.FLAME_TATER);
    public static final Item PUZZLE_CUBE_TATER = registerHead("puzzle_cube_tater", NEBlocks.PUZZLE_CUBE_TATER);
    public static final Item LUCKY_TATER = registerHead("lucky_tater", NEBlocks.LUCKY_TATER);
    public static final Item CRATE_TATER = registerHead("crate_tater", NEBlocks.CRATE_TATER);
    public static final Item TATER_OF_UNDYING = registerHead("tater_of_undying", NEBlocks.TATER_OF_UNDYING);
    public static final Item CRYING_OBSIDIAN_TATER = registerHead("crying_obsidian_tater", NEBlocks.CRYING_OBSIDIAN_TATER);
    public static final Item DICE_TATER = registerHead("dice_tater", NEBlocks.DICE_TATER);
    public static final Item TRANS_TATER = registerHead("trans_tater", NEBlocks.TRANS_TATER);
    public static final Item ASEXUAL_TATER = registerHead("asexual_tater", NEBlocks.ASEXUAL_TATER);
    public static final Item BI_TATER = registerHead("bi_tater", NEBlocks.BI_TATER);
    public static final Item GAY_TATER = registerHead("gay_tater", NEBlocks.GAY_TATER);
    public static final Item LESBIAN_TATER = registerHead("lesbian_tater", NEBlocks.LESBIAN_TATER);
    public static final Item NONBINARY_TATER = registerHead("nonbinary_tater", NEBlocks.NONBINARY_TATER);
    public static final Item PAN_TATER = registerHead("pan_tater", NEBlocks.PAN_TATER);
    public static final Item WARDEN_TATER = registerHead("warden_tater", NEBlocks.WARDEN_TATER);
    public static final Item VIRAL_TATER = registerHead("viral_tater", NEBlocks.VIRAL_TATER);
    public static final Item TATEROID = registerHead("tateroid", NEBlocks.TATEROID);
    public static final Item RED_TATEROID = registerHead("red_tateroid", NEBlocks.RED_TATEROID);
    public static final Item ORANGE_TATEROID = registerHead("orange_tateroid", NEBlocks.ORANGE_TATEROID);
    public static final Item YELLOW_TATEROID = registerHead("yellow_tateroid", NEBlocks.YELLOW_TATEROID);
    public static final Item GREEN_TATEROID = registerHead("green_tateroid", NEBlocks.GREEN_TATEROID);
    public static final Item BLUE_TATEROID = registerHead("blue_tateroid", NEBlocks.BLUE_TATEROID);
    public static final Item PURPLE_TATEROID = registerHead("purple_tateroid", NEBlocks.PURPLE_TATEROID);
    public static final Item FLIPPED_TATER = registerHead("flipped_tater", NEBlocks.FLIPPED_TATER);
    public static final Item BACKWARD_TATER = registerHead("backward_tater", NEBlocks.BACKWARD_TATER);
    public static final Item UPWARD_TATER = registerHead("upward_tater", NEBlocks.UPWARD_TATER);
    public static final Item SANTA_HAT_TATER = registerHead("santa_hat_tater", NEBlocks.SANTA_HAT_TATER);
    public static final Item GENDERFLUID_TATER = registerHead("genderfluid_tater", NEBlocks.GENDERFLUID_TATER);
    public static final Item DEMISEXUAL_TATER = registerHead("demisexual_tater", NEBlocks.DEMISEXUAL_TATER);

    public static final Item SKELETATER = registerHead("skeletater", NEBlocks.SKELETATER);
    public static final Item WITHER_SKELETATER = registerHead("wither_skeletater", NEBlocks.WITHER_SKELETATER);
    public static final Item ZOMBIE_TATER = registerHead("zombie_tater", NEBlocks.ZOMBIE_TATER);
    public static final Item CREEPER_TATER = registerHead("creeper_tater", NEBlocks.CREEPER_TATER);
    public static final Item STEVE_TATER = registerHead("steve_tater", NEBlocks.STEVE_TATER);
    public static final Item ALEX_TATER = registerHead("alex_tater", NEBlocks.ALEX_TATER);

    public static final Item WHITE_TATER = registerHead("white_tater", NEBlocks.WHITE_TATER);
    public static final Item ORANGE_TATER = registerHead("orange_tater", NEBlocks.ORANGE_TATER);
    public static final Item MAGENTA_TATER = registerHead("magenta_tater", NEBlocks.MAGENTA_TATER);
    public static final Item LIGHT_BLUE_TATER = registerHead("light_blue_tater", NEBlocks.LIGHT_BLUE_TATER);
    public static final Item YELLOW_TATER = registerHead("yellow_tater", NEBlocks.YELLOW_TATER);
    public static final Item LIME_TATER = registerHead("lime_tater", NEBlocks.LIME_TATER);
    public static final Item PINK_TATER = registerHead("pink_tater", NEBlocks.PINK_TATER);
    public static final Item GRAY_TATER = registerHead("gray_tater", NEBlocks.GRAY_TATER);
    public static final Item LIGHT_GRAY_TATER = registerHead("light_gray_tater", NEBlocks.LIGHT_GRAY_TATER);
    public static final Item CYAN_TATER = registerHead("cyan_tater", NEBlocks.CYAN_TATER);
    public static final Item PURPLE_TATER = registerHead("purple_tater", NEBlocks.PURPLE_TATER);
    public static final Item BLUE_TATER = registerHead("blue_tater", NEBlocks.BLUE_TATER);
    public static final Item BROWN_TATER = registerHead("brown_tater", NEBlocks.BROWN_TATER);
    public static final Item GREEN_TATER = registerHead("green_tater", NEBlocks.GREEN_TATER);
    public static final Item RED_TATER = registerHead("red_tater", NEBlocks.RED_TATER);
    public static final Item BLACK_TATER = registerHead("black_tater", NEBlocks.BLACK_TATER);

    public static final Item COAL_TATER = registerHead("coal_tater", NEBlocks.COAL_TATER);
    public static final Item DIAMOND_TATER = registerHead("diamond_tater", NEBlocks.DIAMOND_TATER);
    public static final Item EMERALD_TATER = registerHead("emerald_tater", NEBlocks.EMERALD_TATER);
    public static final Item GOLD_TATER = registerHead("gold_tater", NEBlocks.GOLD_TATER);
    public static final Item IRON_TATER = registerHead("iron_tater", NEBlocks.IRON_TATER);
    public static final Item LAPIS_TATER = registerHead("lapis_tater", NEBlocks.LAPIS_TATER);
    public static final Item NETHERITE_TATER = registerHead("netherite_tater", NEBlocks.NETHERITE_TATER);
    public static final Item QUARTZ_TATER = registerHead("quartz_tater", NEBlocks.QUARTZ_TATER);
    public static final Item REDSTONE_TATER = registerHead("redstone_tater", NEBlocks.REDSTONE_TATER);

    public static final Item COPPER_TATER = registerHead("copper_tater", NEBlocks.COPPER_TATER);
    public static final Item EXPOSED_COPPER_TATER = registerHead("exposed_copper_tater", NEBlocks.EXPOSED_COPPER_TATER);
    public static final Item WEATHERED_COPPER_TATER = registerHead("weathered_copper_tater", NEBlocks.WEATHERED_COPPER_TATER);
    public static final Item OXIDIZED_COPPER_TATER = registerHead("oxidized_copper_tater", NEBlocks.OXIDIZED_COPPER_TATER);

    public static final Item CAKE_TATER = registerHead("cake_tater", NEBlocks.CAKE_TATER);
    public static final Item ENDERTATER = registerHead("endertater", NEBlocks.ENDERTATER);
    public static final Item FURNACE_TATER = registerHead("furnace_tater", NEBlocks.FURNACE_TATER);
    public static final Item MELON_TATER = registerHead("melon_tater", NEBlocks.MELON_TATER);
    public static final Item PUMPKIN_TATER = registerHead("pumpkin_tater", NEBlocks.PUMPKIN_TATER);
    public static final Item JACK_O_TATER = registerHead("jack_o_tater", NEBlocks.JACK_O_TATER);
    public static final Item SCULK_TATER = registerHead("sculk_tater", NEBlocks.SCULK_TATER);
    public static final Item SLIME_TATER = registerHead("slime_tater", NEBlocks.SLIME_TATER);
    public static final Item HEROBRINE_TATER = registerHead("herobrine_tater", NEBlocks.HEROBRINE_TATER);
    public static final Item OCHRE_FROGLIGHT_TATER = registerHead("ochre_froglight_tater", NEBlocks.OCHRE_FROGLIGHT_TATER);
    public static final Item PEARLESCENT_FROGLIGHT_TATER = registerHead("pearlescent_froglight_tater", NEBlocks.PEARLESCENT_FROGLIGHT_TATER);
    public static final Item VERDANT_FROGLIGHT_TATER = registerHead("verdant_froglight_tater", NEBlocks.VERDANT_FROGLIGHT_TATER);
    public static final Item SNOWMAN_TATER = registerHead("snowman_tater", NEBlocks.SNOWMAN_TATER);

    public static final Item ACACIA_TATER = registerHead("acacia_tater", NEBlocks.ACACIA_TATER);
    public static final Item ANDESITE_TATER = registerHead("andesite_tater", NEBlocks.ANDESITE_TATER);
    public static final Item BAMBOO_TATER = registerHead("bamboo_tater", NEBlocks.BAMBOO_TATER);
    public static final Item BARRIER_TATER = registerHead("barrier_tater", NEBlocks.BARRIER_TATER);
    public static final Item BEDROCK_TATER = registerHead("bedrock_tater", NEBlocks.BEDROCK_TATER);
    public static final Item BIRCH_TATER = registerHead("birch_tater", NEBlocks.BIRCH_TATER);
    public static final Item BONE_TATER = registerHead("bone_tater", NEBlocks.BONE_TATER);
    public static final Item BRAIN_CORAL_TATER = registerHead("brain_coral_tater", NEBlocks.BRAIN_CORAL_TATER);
    public static final Item BRICK_TATER = registerHead("brick_tater", NEBlocks.BRICK_TATER);
    public static final Item BUBBLE_CORAL_TATER = registerHead("bubble_coral_tater", NEBlocks.BUBBLE_CORAL_TATER);
    public static final Item CACTUS_TATER = registerHead("cactus_tater", NEBlocks.CACTUS_TATER);
    public static final Item CHORUS_TATER = registerHead("chorus_tater", NEBlocks.CHORUS_TATER);
    public static final Item CLAY_TATER = registerHead("clay_tater", NEBlocks.CLAY_TATER);
    public static final Item CRIMSON_TATER = registerHead("crimson_tater", NEBlocks.CRIMSON_TATER);
    public static final Item DARK_OAK_TATER = registerHead("dark_oak_tater", NEBlocks.DARK_OAK_TATER);
    public static final Item DARK_PRISMARINE_TATER = registerHead("dark_prismarine_tater", NEBlocks.DARK_PRISMARINE_TATER);
    public static final Item DIORITE_TATER = registerHead("diorite_tater", NEBlocks.DIORITE_TATER);
    public static final Item DIRT_TATER = registerHead("dirt_tater", NEBlocks.DIRT_TATER);
    public static final Item END_STONE_TATER = registerHead("end_stone_tater", NEBlocks.END_STONE_TATER);
    public static final Item FIRE_CORAL_TATER = registerHead("fire_coral_tater", NEBlocks.FIRE_CORAL_TATER);
    public static final Item GRANITE_TATER = registerHead("granite_tater", NEBlocks.GRANITE_TATER);
    public static final Item GRASS_TATER = registerHead("grass_tater", NEBlocks.GRASS_TATER);
    public static final Item HAY_TATER = registerHead("hay_tater", NEBlocks.HAY_TATER);
    public static final Item HONEY_TATER = registerHead("honey_tater", NEBlocks.HONEY_TATER);
    public static final Item HONEYCOMB_TATER = registerHead("honeycomb_tater", NEBlocks.HONEYCOMB_TATER);
    public static final Item HORN_CORAL_TATER = registerHead("horn_coral_tater", NEBlocks.HORN_CORAL_TATER);
    public static final Item JUNGLE_TATER = registerHead("jungle_tater", NEBlocks.JUNGLE_TATER);
    public static final Item LIGHT_TATER = registerHead("light_tater", NEBlocks.LIGHT_TATER);
    public static final Item MYCELIUM_TATER = registerHead("mycelium_tater", NEBlocks.MYCELIUM_TATER);
    public static final Item NETHER_WART_TATER = registerHead("nether_wart_tater", NEBlocks.NETHER_WART_TATER);
    public static final Item OAK_TATER = registerHead("oak_tater", NEBlocks.OAK_TATER);
    public static final Item OBSIDIAN_TATER = registerHead("obsidian_tater", NEBlocks.OBSIDIAN_TATER);
    public static final Item PODZOL_TATER = registerHead("podzol_tater", NEBlocks.PODZOL_TATER);
    public static final Item PRISMARINE_BRICK_TATER = registerHead("prismarine_brick_tater", NEBlocks.PRISMARINE_BRICK_TATER);
    public static final Item PRISMARINE_TATER = registerHead("prismarine_tater", NEBlocks.PRISMARINE_TATER);
    public static final Item RED_SAND_TATER = registerHead("red_sand_tater", NEBlocks.RED_SAND_TATER);
    public static final Item SAND_TATER = registerHead("sand_tater", NEBlocks.SAND_TATER);
    public static final Item SEA_LANTERN_TATER = registerHead("sea_lantern_tater", NEBlocks.SEA_LANTERN_TATER);
    public static final Item SHROOMLIGHT_TATER = registerHead("shroomlight_tater", NEBlocks.SHROOMLIGHT_TATER);
    public static final Item SHULKER_TATER = registerHead("shulker_tater", NEBlocks.SHULKER_TATER);
    public static final Item SMOOTH_STONE_TATER = registerHead("smooth_stone_tater", NEBlocks.SMOOTH_STONE_TATER);
    public static final Item SOUL_SAND_TATER = registerHead("soul_sand_tater", NEBlocks.SOUL_SAND_TATER);
    public static final Item SPONGE_TATER = registerHead("sponge_tater", NEBlocks.SPONGE_TATER);
    public static final Item SPRUCE_TATER = registerHead("spruce_tater", NEBlocks.SPRUCE_TATER);
    public static final Item STONE_BRICK_TATER = registerHead("stone_brick_tater", NEBlocks.STONE_BRICK_TATER);
    public static final Item STRUCTURE_VOID_TATER = registerHead("structure_void_tater", NEBlocks.STRUCTURE_VOID_TATER);
    public static final Item TARGET_TATER = registerHead("target_tater", NEBlocks.TARGET_TATER);
    public static final Item TERRACOTTA_TATER = registerHead("terracotta_tater", NEBlocks.TERRACOTTA_TATER);
    public static final Item TNTATER = registerHead("tntater", NEBlocks.TNTATER);
    public static final Item TUBE_CORAL_TATER = registerHead("tube_coral_tater", NEBlocks.TUBE_CORAL_TATER);
    public static final Item WARPED_TATER = registerHead("warped_tater", NEBlocks.WARPED_TATER);
    public static final Item WARPED_WART_TATER = registerHead("warped_wart_tater", NEBlocks.WARPED_WART_TATER);
    public static final Item WOOL_TATER = registerHead("wool_tater", NEBlocks.WOOL_TATER);

    public static final Item ACACIA_LOG_TATER = registerHead("acacia_log_tater", NEBlocks.ACACIA_LOG_TATER);
    public static final Item ANGRY_BEE_TATER = registerHead("angry_bee_tater", NEBlocks.ANGRY_BEE_TATER);
    public static final Item BEACON_TATER = registerHead("beacon_tater", NEBlocks.BEACON_TATER);
    public static final Item BEE_NEST_TATER = registerHead("bee_nest_tater", NEBlocks.BEE_NEST_TATER);
    public static final Item BEE_TATER = registerHead("bee_tater", NEBlocks.BEE_TATER);
    public static final Item BEEHIVE_TATER = registerHead("beehive_tater", NEBlocks.BEEHIVE_TATER);
    public static final Item BIRCH_LOG_TATER = registerHead("birch_log_tater", NEBlocks.BIRCH_LOG_TATER);
    public static final Item BLACKSTONE_TATER = registerHead("blackstone_tater", NEBlocks.BLACKSTONE_TATER);
    public static final Item BLAZE_TATER = registerHead("blaze_tater", NEBlocks.BLAZE_TATER);
    public static final Item BOOKSHELF_TATER = registerHead("bookshelf_tater", NEBlocks.BOOKSHELF_TATER);
    public static final Item BROWN_MOOSHROOM_TATER = registerHead("brown_mooshroom_tater", NEBlocks.BROWN_MOOSHROOM_TATER);
    public static final Item BROWN_MUSHROOM_TATER = registerHead("brown_mushroom_tater", NEBlocks.BROWN_MUSHROOM_TATER);
    public static final Item CAVE_SPIDER_TATER = registerHead("cave_spider_tater", NEBlocks.CAVE_SPIDER_TATER);
    public static final Item COBBLED_DEEPSLATE_TATER = registerHead("cobbled_deepslate_tater", NEBlocks.COBBLED_DEEPSLATE_TATER);
    public static final Item COBBLESTONE_TATER = registerHead("cobblestone_tater", NEBlocks.COBBLESTONE_TATER);
    public static final Item COCOA_TATER = registerHead("cocoa_tater", NEBlocks.COCOA_TATER);
    public static final Item COLD_STRIDER_TATER = registerHead("cold_strider_tater", NEBlocks.COLD_STRIDER_TATER);
    public static final Item COW_TATER = registerHead("cow_tater", NEBlocks.COW_TATER);
    public static final Item CRAFTING_TATER = registerHead("crafting_tater", NEBlocks.CRAFTING_TATER);
    public static final Item CRIMSON_NYLIUM_TATER = registerHead("crimson_nylium_tater", NEBlocks.CRIMSON_NYLIUM_TATER);
    public static final Item CRIMSON_STEM_TATER = registerHead("crimson_stem_tater", NEBlocks.CRIMSON_STEM_TATER);
    public static final Item DARK_OAK_LOG_TATER = registerHead("dark_oak_log_tater", NEBlocks.DARK_OAK_LOG_TATER);
    public static final Item DAYLIGHT_DETECTOR_TATER = registerHead("daylight_detector_tater", NEBlocks.DAYLIGHT_DETECTOR_TATER);
    public static final Item DEAD_BRAIN_CORAL_TATER = registerHead("dead_brain_coral_tater", NEBlocks.DEAD_BRAIN_CORAL_TATER);
    public static final Item DEAD_BUBBLE_CORAL_TATER = registerHead("dead_bubble_coral_tater", NEBlocks.DEAD_BUBBLE_CORAL_TATER);
    public static final Item DEAD_FIRE_CORAL_TATER = registerHead("dead_fire_coral_tater", NEBlocks.DEAD_FIRE_CORAL_TATER);
    public static final Item DEAD_HORN_CORAL_TATER = registerHead("dead_horn_coral_tater", NEBlocks.DEAD_HORN_CORAL_TATER);
    public static final Item DEAD_TUBE_CORAL_TATER = registerHead("dead_tube_coral_tater", NEBlocks.DEAD_TUBE_CORAL_TATER);
    public static final Item DEEPSLATE_BRICK_TATER = registerHead("deepslate_brick_tater", NEBlocks.DEEPSLATE_BRICK_TATER);
    public static final Item DEEPSLATE_TATER = registerHead("deepslate_tater", NEBlocks.DEEPSLATE_TATER);
    public static final Item DRIED_KELP_TATER = registerHead("dried_kelp_tater", NEBlocks.DRIED_KELP_TATER);
    public static final Item DROWNED_TATER = registerHead("drowned_tater", NEBlocks.DROWNED_TATER);
    public static final Item EYE_OF_ENDER_TATER = registerHead("eye_of_ender_tater", NEBlocks.EYE_OF_ENDER_TATER);
    public static final Item FOX_TATER = registerHead("fox_tater", NEBlocks.FOX_TATER);
    public static final Item GHAST_TATER = registerHead("ghast_tater", NEBlocks.GHAST_TATER);
    public static final Item GILDED_BLACKSTONE_TATER = registerHead("gilded_blackstone_tater", NEBlocks.GILDED_BLACKSTONE_TATER);
    public static final Item GLOW_SQUID_TATER = registerHead("glow_squid_tater", NEBlocks.GLOW_SQUID_TATER);
    public static final Item GLOWSTONE_TATER = registerHead("glowstone_tater", NEBlocks.GLOWSTONE_TATER);
    public static final Item HUSK_TATER = registerHead("husk_tater", NEBlocks.HUSK_TATER);
    public static final Item INVERTED_DAYLIGHT_DETECTOR_TATER = registerHead("inverted_daylight_detector_tater", NEBlocks.INVERTED_DAYLIGHT_DETECTOR_TATER);
    public static final Item JUNGLE_LOG_TATER = registerHead("jungle_log_tater", NEBlocks.JUNGLE_LOG_TATER);
    public static final Item MAGMA_CUBE_TATER = registerHead("magma_cube_tater", NEBlocks.MAGMA_CUBE_TATER);
    public static final Item MOOBLOOM_TATER = registerHead("moobloom_tater", NEBlocks.MOOBLOOM_TATER);
    public static final Item MOOLIP_TATER = registerHead("moolip_tater", NEBlocks.MOOLIP_TATER);
    public static final Item MUDDY_PIG_TATER = registerHead("muddy_pig_tater", NEBlocks.MUDDY_PIG_TATER);
    public static final Item MUSHROOM_STEM_TATER = registerHead("mushroom_stem_tater", NEBlocks.MUSHROOM_STEM_TATER);
    public static final Item NETHER_BRICK_TATER = registerHead("nether_brick_tater", NEBlocks.NETHER_BRICK_TATER);
    public static final Item NETHERRACK_TATER = registerHead("netherrack_tater", NEBlocks.NETHERRACK_TATER);
    public static final Item OAK_LOG_TATER = registerHead("oak_log_tater", NEBlocks.OAK_LOG_TATER);
    public static final Item PIG_TATER = registerHead("pig_tater", NEBlocks.PIG_TATER);
    public static final Item POLAR_BEAR_TATER = registerHead("polar_bear_tater", NEBlocks.POLAR_BEAR_TATER);
    public static final Item PUFFERTATER = registerHead("puffertater", NEBlocks.PUFFERTATER);
    public static final Item RED_MOOSHROOM_TATER = registerHead("red_mooshroom_tater", NEBlocks.RED_MOOSHROOM_TATER);
    public static final Item RED_MUSHROOM_TATER = registerHead("red_mushroom_tater", NEBlocks.RED_MUSHROOM_TATER);
    public static final Item RED_NETHER_BRICK_TATER = registerHead("red_nether_brick_tater", NEBlocks.RED_NETHER_BRICK_TATER);
    public static final Item RED_SANDSTONE_TATER = registerHead("red_sandstone_tater", NEBlocks.RED_SANDSTONE_TATER);
    public static final Item RUBY_TATER = registerHead("ruby_tater", NEBlocks.RUBY_TATER);
    public static final Item SANDSTONE_TATER = registerHead("sandstone_tater", NEBlocks.SANDSTONE_TATER);
    public static final Item SEA_PICKLE_TATER = registerHead("sea_pickle_tater", NEBlocks.SEA_PICKLE_TATER);
    public static final Item SHEEP_TATER = registerHead("sheep_tater", NEBlocks.SHEEP_TATER);
    public static final Item SHIELDED_WITHER_TATER = registerHead("shielded_wither_tater", NEBlocks.SHIELDED_WITHER_TATER);
    public static final Item SNOW_FOX_TATER = registerHead("snow_fox_tater", NEBlocks.SNOW_FOX_TATER);
    public static final Item SPIDER_TATER = registerHead("spider_tater", NEBlocks.SPIDER_TATER);
    public static final Item SPRUCE_LOG_TATER = registerHead("spruce_log_tater", NEBlocks.SPRUCE_LOG_TATER);
    public static final Item SQUID_TATER = registerHead("squid_tater", NEBlocks.SQUID_TATER);
    public static final Item STRAY_TATER = registerHead("stray_tater", NEBlocks.STRAY_TATER);
    public static final Item STRIDER_TATER = registerHead("strider_tater", NEBlocks.STRIDER_TATER);
    public static final Item TURTLE_EGG_TATER = registerHead("turtle_egg_tater", NEBlocks.TURTLE_EGG_TATER);
    public static final Item WARPED_NYLIUM_TATER = registerHead("warped_nylium_tater", NEBlocks.WARPED_NYLIUM_TATER);
    public static final Item WARPED_STEM_TATER = registerHead("warped_stem_tater", NEBlocks.WARPED_STEM_TATER);
    public static final Item WITHER_TATER = registerHead("wither_tater", NEBlocks.WITHER_TATER);

    public static final Item AZALEA_TATER = registerHead("azalea_tater", NEBlocks.AZALEA_TATER);
    public static final Item BELL_TATER = registerHead("bell_tater", NEBlocks.BELL_TATER);
    public static final Item COLD_FROG_TATER = registerHead("cold_frog_tater", NEBlocks.COLD_FROG_TATER);
    public static final Item CONDUIT_TATER = registerHead("conduit_tater", NEBlocks.CONDUIT_TATER);
    public static final Item ELDER_GUARDIAN_TATER = registerHead("elder_guardian_tater", NEBlocks.ELDER_GUARDIAN_TATER);
    public static final Item END_STONE_BRICK_TATER = registerHead("end_stone_brick_tater", NEBlocks.END_STONE_BRICK_TATER);
    public static final Item FLOWER_POT_TATER = registerHead("flower_pot_tater", NEBlocks.FLOWER_POT_TATER);
    public static final Item GUARDIAN_TATER = registerHead("guardian_tater", NEBlocks.GUARDIAN_TATER);
    public static final Item ILLAGER_TATER = registerHead("illager_tater", NEBlocks.ILLAGER_TATER);
    public static final Item ILLUSIONER_TATER = registerHead("illusioner_tater", NEBlocks.ILLUSIONER_TATER);
    public static final Item JUKEBOX_TATER = registerHead("jukebox_tater", NEBlocks.JUKEBOX_TATER);
    public static final Item LANTERN_TATER = registerHead("lantern_tater", NEBlocks.LANTERN_TATER);
    public static final Item PIGLIN_TATER = registerHead("piglin_tater", NEBlocks.PIGLIN_TATER);
    public static final Item PINK_WITHER_TATER = registerHead("pink_wither_tater", NEBlocks.PINK_WITHER_TATER);
    public static final Item PISTON_TATER = registerHead("piston_tater", NEBlocks.PISTON_TATER);
    public static final Item PURPUR_TATER = registerHead("purpur_tater", NEBlocks.PURPUR_TATER);
    public static final Item SOUL_LANTERN_TATER = registerHead("soul_lantern_tater", NEBlocks.SOUL_LANTERN_TATER);
    public static final Item SOUL_SOIL_TATER = registerHead("soul_soil_tater", NEBlocks.SOUL_SOIL_TATER);
    public static final Item STICKY_PISTON_TATER = registerHead("sticky_piston_tater", NEBlocks.STICKY_PISTON_TATER);
    public static final Item TEMPERATE_FROG_TATER = registerHead("temperate_frog_tater", NEBlocks.TEMPERATE_FROG_TATER);
    public static final Item UNDERWATER_TNTATER = registerHead("underwater_tntater", NEBlocks.UNDERWATER_TNTATER);
    public static final Item VEX_TATER = registerHead("vex_tater", NEBlocks.VEX_TATER);
    public static final Item VILLAGER_TATER = registerHead("villager_tater", NEBlocks.VILLAGER_TATER);
    public static final Item VINDITATER = registerHead("vinditater", NEBlocks.VINDITATER);
    public static final Item WANDERING_TRADER_TATER = registerHead("wandering_trader_tater", NEBlocks.WANDERING_TRADER_TATER);
    public static final Item WARM_FROG_TATER = registerHead("warm_frog_tater", NEBlocks.WARM_FROG_TATER);
    public static final Item WAX_TATER = registerHead("wax_tater", NEBlocks.WAX_TATER);
    public static final Item WITCH_TATER = registerHead("witch_tater", NEBlocks.WITCH_TATER);
    public static final Item ZOMBIE_VILLAGER_TATER = registerHead("zombie_villager_tater", NEBlocks.ZOMBIE_VILLAGER_TATER);
    public static final Item ZOMBIFIED_PIGLIN_TATER = registerHead("zombified_piglin_tater", NEBlocks.ZOMBIFIED_PIGLIN_TATER);

    public static final Item BONE_SPIDER_TATER = registerHead("bone_spider_tater", NEBlocks.BONE_SPIDER_TATER);
    public static final Item BOULDERING_ZOMBIE_TATER = registerHead("bouldering_zombie_tater", NEBlocks.BOULDERING_ZOMBIE_TATER);
    public static final Item CHARGED_CREEPER_TATER = registerHead("charged_creeper_tater", NEBlocks.CHARGED_CREEPER_TATER);
    public static final Item LOBBER_ZOMBIE_TATER = registerHead("lobber_zombie_tater", NEBlocks.LOBBER_ZOMBIE_TATER);
    public static final Item MOSSY_SKELETATER = registerHead("mossy_skeletater", NEBlocks.MOSSY_SKELETATER);
    public static final Item STRIPPED_ACACIA_LOG_TATER = registerHead("stripped_acacia_log_tater", NEBlocks.STRIPPED_ACACIA_LOG_TATER);
    public static final Item STRIPPED_BIRCH_LOG_TATER = registerHead("stripped_birch_log_tater", NEBlocks.STRIPPED_BIRCH_LOG_TATER);
    public static final Item STRIPPED_CRIMSON_STEM_TATER = registerHead("stripped_crimson_stem_tater", NEBlocks.STRIPPED_CRIMSON_STEM_TATER);
    public static final Item STRIPPED_DARK_OAK_LOG_TATER = registerHead("stripped_dark_oak_log_tater", NEBlocks.STRIPPED_DARK_OAK_LOG_TATER);
    public static final Item STRIPPED_JUNGLE_LOG_TATER = registerHead("stripped_jungle_log_tater", NEBlocks.STRIPPED_JUNGLE_LOG_TATER);
    public static final Item STRIPPED_OAK_LOG_TATER = registerHead("stripped_oak_log_tater", NEBlocks.STRIPPED_OAK_LOG_TATER);
    public static final Item STRIPPED_SPRUCE_LOG_TATER = registerHead("stripped_spruce_log_tater", NEBlocks.STRIPPED_SPRUCE_LOG_TATER);
    public static final Item STRIPPED_WARPED_STEM_TATER = registerHead("stripped_warped_stem_tater", NEBlocks.STRIPPED_WARPED_STEM_TATER);
    public static final Item TROPICAL_SLIME_TATER = registerHead("tropical_slime_tater", NEBlocks.TROPICAL_SLIME_TATER);

    public static final Item APPLE_TATER = registerHead("apple_tater", NEBlocks.APPLE_TATER);
    public static final Item GOLDEN_APPLE_TATER = registerHead("golden_apple_tater", NEBlocks.GOLDEN_APPLE_TATER);
    public static final Item ICE_TATER = registerHead("ice_tater", NEBlocks.ICE_TATER);
    public static final Item KING_TATER = registerHead("king_tater", NEBlocks.KING_TATER);
    public static final Item RAW_COPPER_TATER = registerHead("raw_copper_tater", NEBlocks.RAW_COPPER_TATER);
    public static final Item RAW_GOLD_TATER = registerHead("raw_gold_tater", NEBlocks.RAW_GOLD_TATER);
    public static final Item RAW_IRON_TATER = registerHead("raw_iron_tater", NEBlocks.RAW_IRON_TATER);

    public static final Item ALLAY_TATER = registerHead("allay_tater", NEBlocks.ALLAY_TATER);
    public static final Item MANGROVE_LOG_TATER = registerHead("mangrove_log_tater", NEBlocks.MANGROVE_LOG_TATER);
    public static final Item MANGROVE_TATER = registerHead("mangrove_tater", NEBlocks.MANGROVE_TATER);
    public static final Item MUD_BRICK_TATER = registerHead("mud_brick_tater", NEBlocks.MUD_BRICK_TATER);
    public static final Item MUD_TATER = registerHead("mud_tater", NEBlocks.MUD_TATER);
    public static final Item PACKED_MUD_TATER = registerHead("packed_mud_tater", NEBlocks.PACKED_MUD_TATER);
    public static final Item STRIPPED_MANGROVE_LOG_TATER = registerHead("stripped_mangrove_log_tater", NEBlocks.STRIPPED_MANGROVE_LOG_TATER);

    public static final Item LUCY_AXOLOTL_TATER = registerHead("lucy_axolotl_tater", NEBlocks.LUCY_AXOLOTL_TATER);
    public static final Item WILD_AXOLOTL_TATER = registerHead("wild_axolotl_tater", NEBlocks.WILD_AXOLOTL_TATER);
    public static final Item GOLD_AXOLOTL_TATER = registerHead("gold_axolotl_tater", NEBlocks.GOLD_AXOLOTL_TATER);
    public static final Item CYAN_AXOLOTL_TATER = registerHead("cyan_axolotl_tater", NEBlocks.CYAN_AXOLOTL_TATER);
    public static final Item BLUE_AXOLOTL_TATER = registerHead("blue_axolotl_tater", NEBlocks.BLUE_AXOLOTL_TATER);

    public static final Item BRONZE_CAPSULE_TATER = registerHead("bronze_capsule_tater", NEBlocks.BRONZE_CAPSULE_TATER);
    public static final Item SILVER_CAPSULE_TATER = registerHead("silver_capsule_tater", NEBlocks.SILVER_CAPSULE_TATER);
    public static final Item GOLD_CAPSULE_TATER = registerHead("gold_capsule_tater", NEBlocks.GOLD_CAPSULE_TATER);

    public static final Item CORRUPTATER = registerHead("corruptater", NEBlocks.CORRUPTATER);

    public static final Item TATER_BOX = register("tater_box", new Item.Settings()
            .component(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT)
            .maxCount(1), settings -> new TaterBoxItem(settings));
    public static final Item CREATIVE_TATER_BOX = register("creative_tater_box", new Item.Settings()
            .component(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT)
            .maxCount(1), settings -> new CreativeTaterBoxItem(settings));

    public static final Item TATER_GUIDEBOOK = register("tater_guidebook", new Item.Settings()
            .component(NEDataComponentTypes.TATER_POSITIONS, TaterPositionsComponent.DEFAULT)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .maxCount(1), settings -> new TaterGuidebookItem(settings));
    public static final Item QUICK_ARMOR_STAND = register("quick_armor_stand", new Item.Settings(), settings -> new QuickArmorStandItem(settings));
    public static final Item GAME_PORTAL_OPENER = register("game_portal_opener", new Item.Settings().maxCount(1), settings -> new GamePortalOpenerItem(settings));
    public static final Item LAUNCH_FEATHER = register("launch_feather", new Item.Settings()
            .component(NEDataComponentTypes.LAUNCHER, LauncherComponent.DEFAULT)
            .maxCount(1), settings -> new LaunchFeatherItem(settings));

    public static final Item RULE_BOOK = register("rule_book", new Item.Settings()
            .rarity(Rarity.EPIC)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true), settings -> new RuleBookItem(settings));

    private static Item registerHead(String id, Block head) {
        Item.Settings baseSettings = new Item.Settings()
                .useBlockPrefixedTranslationKey()
                .equippableUnswappable(EquipmentSlot.HEAD);

        if (head instanceof TinyPotatoBlock tinyPotatoBlock) {
            return register(id, baseSettings, settings -> new LobbyHeadItem(head, settings, tinyPotatoBlock.getItemTexture()));
        } else if (head instanceof PolymerHeadBlock headBlock) {
            return register(id, baseSettings, settings -> new LobbyHeadItem(head, settings, headBlock.getPolymerSkinValue(head.getDefaultState(), BlockPos.ORIGIN, null)));
        }

        throw new IllegalArgumentException("Cannot register " + id + " as a head item because " + head + " is not a head");
    }

    private static Item registerSimple(String id, Block block, Item virtual) {
        return register(id, new Item.Settings().component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true).useBlockPrefixedTranslationKey(), settings -> new LobbyBlockItem(block, settings, virtual));
    }

    public static void register() {
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

        if (config.rules() != null) {
            tryOfferStack(player, RULE_BOOK);
        }

        config.gamePortalOpener().ifPresent(gamePortal -> {
            tryOfferStack(player, GAME_PORTAL_OPENER, stack -> {
                stack.set(NEDataComponentTypes.GAME_PORTAL, new GamePortalComponent(gamePortal));
            });
        });
    }

    public static boolean canUseTaters(ServerPlayerEntity player) {
        return !GameSpaceManager.get().inGame(player);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!player.getWorld().isClient() && hitResult != null && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();

            PlayerLobbyState state = PlayerLobbyState.get(player);
            state.collectTaterFromBlock(world, pos, stack, (ServerPlayerEntity) player);
        }

        return ActionResult.PASS;
    }

    private static ActionResult onUseEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (!player.getWorld().isClient() && hitResult != null) {
            ItemStack stack = player.getStackInHand(hand);
            Vec3d hitPos = hitResult.getPos().subtract(entity.getPos());

            PlayerLobbyState state = PlayerLobbyState.get(player);
            state.collectTaterFromEntity(entity, hitPos, stack, (ServerPlayerEntity) player);
        }

        return ActionResult.PASS;
    }

    private static <T extends Item> T register(String id, Item.Settings settings, Function<Item.Settings, T> factory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, NucleoidExtras.identifier(id));
        T item = factory.apply(settings.registryKey(key));

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof TinyPotatoBlock) {
            TATERS.add(item);
        }

        return Registry.register(Registries.ITEM, key, item);
    }
}
