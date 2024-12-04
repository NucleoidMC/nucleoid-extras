package xyz.nucleoid.extras.data.provider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements.CriterionMerger;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.NECriteria;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.criterion.TaterCollectedCriterion;
import xyz.nucleoid.extras.lobby.criterion.TaterCount;
import xyz.nucleoid.extras.lobby.criterion.WearTaterCriterion;

public class NEAdvancementProvider extends FabricAdvancementProvider {
    public NEAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registries, Consumer<AdvancementEntry> consumer) {
        var root = accept(consumer, "root", null, Advancement.Builder.createUntelemetered()
                .display(
                        NEItems.NUCLEOID_LOGO,
                        Text.translatable("advancements.nucleoid_extras.root.title"),
                        Text.translatable("advancements.nucleoid_extras.root.description"),
                        Identifier.ofVanilla("textures/block/lime_concrete.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("get_tater", NECriteria.TATER_COLLECTED.create(
                        new TaterCollectedCriterion.Conditions(Optional.empty(), Optional.of(new TaterCount.Value(1)))
                ))
        );

        // Based on number collected
        var firstTater = accept(consumer, "first_tater", NEBlocks.TINY_POTATO, requiringTatersCollected(1).parent(root));
        var tenTaters = accept(consumer, "ten_taters", NEBlocks.IRON_TATER, AdvancementFrame.GOAL, requiringTatersCollected(10).parent(firstTater));
        var twentyFiveTaters = accept(consumer, "twenty_five_taters", NEBlocks.GOLD_TATER, AdvancementFrame.GOAL, requiringTatersCollected(25).parent(tenTaters));
        var fiftyTaters = accept(consumer, "fifty_taters", NEBlocks.DIAMOND_TATER, AdvancementFrame.GOAL, requiringTatersCollected(50).parent(twentyFiveTaters));
        var oneHundredTaters = accept(consumer, "one_hundred_taters", NEBlocks.EMERALD_TATER, AdvancementFrame.GOAL, requiringTatersCollected(100).parent(fiftyTaters));
        var twoHundredTaters = accept(consumer, "two_hundred_taters", NEBlocks.NETHERITE_TATER, AdvancementFrame.GOAL, requiringTatersCollected(200).parent(oneHundredTaters));

        accept(consumer, "all_taters", null, requiringTatersCollected(new TaterCount.All())
                .display(
                        NEBlocks.TATER_OF_UNDYING,
                        Text.translatable("advancements.nucleoid_extras.all_taters.title"),
                        Text.translatable("advancements.nucleoid_extras.all_taters.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .parent(twoHundredTaters)
        );

        var copperTaters = accept(consumer, "copper_taters", NEBlocks.COPPER_TATER, requiringTatersCollected(NEBlocks.COPPER_TATER, NEBlocks.EXPOSED_COPPER_TATER, NEBlocks.OXIDIZED_COPPER_TATER, NEBlocks.RAW_COPPER_TATER, NEBlocks.WEATHERED_COPPER_TATER).parent(firstTater));
        var axolotlTaters = accept(consumer, "axolotl_taters", NEBlocks.LUCY_AXOLOTL_TATER, requiringTatersCollected(NEBlocks.BLUE_AXOLOTL_TATER, NEBlocks.CYAN_AXOLOTL_TATER, NEBlocks.GOLD_AXOLOTL_TATER, NEBlocks.LUCY_AXOLOTL_TATER, NEBlocks.WILD_AXOLOTL_TATER).parent(copperTaters));
        var coralTaters = accept(consumer, "coral_taters", NEBlocks.BRAIN_CORAL_TATER, requiringTatersCollected(NEBlocks.BRAIN_CORAL_TATER, NEBlocks.BUBBLE_CORAL_TATER, NEBlocks.DEAD_BRAIN_CORAL_TATER, NEBlocks.DEAD_BUBBLE_CORAL_TATER, NEBlocks.DEAD_FIRE_CORAL_TATER, NEBlocks.DEAD_HORN_CORAL_TATER, NEBlocks.DEAD_TUBE_CORAL_TATER, NEBlocks.FIRE_CORAL_TATER, NEBlocks.HORN_CORAL_TATER, NEBlocks.TUBE_CORAL_TATER).parent(tenTaters));
        accept(consumer, "dyed_taters", NEBlocks.RED_TATER, requiringTatersCollected(NEBlocks.BLACK_TATER, NEBlocks.BLUE_TATER, NEBlocks.BROWN_TATER, NEBlocks.CYAN_TATER, NEBlocks.GRAY_TATER, NEBlocks.GREEN_TATER, NEBlocks.LIGHT_BLUE_TATER, NEBlocks.LIGHT_GRAY_TATER, NEBlocks.LIME_TATER, NEBlocks.MAGENTA_TATER, NEBlocks.ORANGE_TATER, NEBlocks.PINK_TATER, NEBlocks.PURPLE_TATER, NEBlocks.RED_TATER, NEBlocks.WHITE_TATER, NEBlocks.YELLOW_TATER).parent(tenTaters));
        accept(consumer, "earth_taters", NEBlocks.MOOBLOOM_TATER, requiringTatersCollected(NEBlocks.BOULDERING_ZOMBIE_TATER, NEBlocks.GLOW_SQUID_TATER, NEBlocks.LOBBER_ZOMBIE_TATER, NEBlocks.MOOBLOOM_TATER, NEBlocks.MOOLIP_TATER, NEBlocks.MUDDY_PIG_TATER, NEBlocks.TROPICAL_SLIME_TATER).parent(firstTater));
        var frogTaters = accept(consumer, "frog_taters", NEBlocks.TEMPERATE_FROG_TATER, requiringTatersCollected(NEBlocks.COLD_FROG_TATER, NEBlocks.TEMPERATE_FROG_TATER, NEBlocks.WARM_FROG_TATER).parent(firstTater));
        var froglightTaters = accept(consumer, "froglight_taters", NEBlocks.OCHRE_FROGLIGHT_TATER, requiringTatersCollected(NEBlocks.OCHRE_FROGLIGHT_TATER, NEBlocks.PEARLESCENT_FROGLIGHT_TATER, NEBlocks.VERDANT_FROGLIGHT_TATER).parent(frogTaters));
        accept(consumer, "ice_taters", NEBlocks.ICE_TATER, requiringTatersCollected(NEBlocks.BLUE_ICE_TATER, NEBlocks.ICE_TATER, NEBlocks.PACKED_ICE_TATER).parent(firstTater));
        accept(consumer, "ore_taters", NEBlocks.COAL_TATER, requiringTatersCollected(NEBlocks.AMETHYST_TATER, NEBlocks.COAL_TATER, NEBlocks.COPPER_TATER, NEBlocks.DIAMOND_TATER, NEBlocks.EMERALD_TATER, NEBlocks.EXPOSED_COPPER_TATER, NEBlocks.GOLD_TATER, NEBlocks.IRON_TATER, NEBlocks.LAPIS_TATER, NEBlocks.NETHERITE_TATER, NEBlocks.OXIDIZED_COPPER_TATER, NEBlocks.QUARTZ_TATER, NEBlocks.RAW_COPPER_TATER, NEBlocks.RAW_GOLD_TATER, NEBlocks.RAW_IRON_TATER, NEBlocks.REDSTONE_TATER, NEBlocks.WEATHERED_COPPER_TATER).parent(copperTaters));
        accept(consumer, "pride_taters", NEBlocks.GAY_TATER, requiringTatersCollected(NEBlocks.ASEXUAL_TATER, NEBlocks.BI_TATER, NEBlocks.DEMISEXUAL_TATER, NEBlocks.GAY_TATER, NEBlocks.GENDERFLUID_TATER, NEBlocks.LESBIAN_TATER, NEBlocks.NONBINARY_TATER, NEBlocks.PAN_TATER, NEBlocks.TRANS_TATER).parent(firstTater));
        accept(consumer, "raider_taters", NEBlocks.ILLAGER_TATER, requiringTatersCollected(NEBlocks.ILLAGER_TATER, NEBlocks.ILLUSIONER_TATER, NEBlocks.VEX_TATER, NEBlocks.VINDITATER, NEBlocks.WITCH_TATER).parent(firstTater));
        accept(consumer, "raw_taters", NEBlocks.RAW_IRON_TATER, requiringTatersCollected(NEBlocks.RAW_COPPER_TATER, NEBlocks.RAW_GOLD_TATER, NEBlocks.RAW_IRON_TATER).parent(firstTater));
        accept(consumer, "skeletaters", NEBlocks.SKELETATER, requiringTatersCollected(NEBlocks.MOSSY_SKELETATER, NEBlocks.SKELETATER, NEBlocks.STRAY_TATER, NEBlocks.WITHER_SKELETATER).parent(firstTater));
        accept(consumer, "slime_taters", NEBlocks.SLIME_TATER, requiringTatersCollected(NEBlocks.MAGMA_CUBE_TATER, NEBlocks.SLIME_TATER, NEBlocks.TROPICAL_SLIME_TATER).parent(firstTater));
        accept(consumer, "tateroids", NEBlocks.TATEROID, requiringTatersCollected(NEBlocks.BLUE_TATEROID, NEBlocks.GREEN_TATEROID, NEBlocks.ORANGE_TATEROID, NEBlocks.PURPLE_TATEROID, NEBlocks.RED_TATEROID, NEBlocks.TATEROID, NEBlocks.YELLOW_TATEROID).parent(firstTater));
        accept(consumer, "wood_taters", NEBlocks.OAK_TATER, requiringTatersCollected(NEBlocks.ACACIA_LOG_TATER, NEBlocks.ACACIA_TATER, NEBlocks.BIRCH_LOG_TATER, NEBlocks.BIRCH_TATER, NEBlocks.CRIMSON_STEM_TATER, NEBlocks.CRIMSON_TATER, NEBlocks.DARK_OAK_LOG_TATER, NEBlocks.DARK_OAK_TATER, NEBlocks.JUNGLE_LOG_TATER, NEBlocks.JUNGLE_TATER, NEBlocks.MANGROVE_LOG_TATER, NEBlocks.MANGROVE_TATER, NEBlocks.OAK_LOG_TATER, NEBlocks.OAK_TATER, NEBlocks.SPRUCE_LOG_TATER, NEBlocks.SPRUCE_TATER, NEBlocks.STRIPPED_ACACIA_LOG_TATER, NEBlocks.STRIPPED_BIRCH_LOG_TATER, NEBlocks.STRIPPED_CRIMSON_STEM_TATER, NEBlocks.STRIPPED_DARK_OAK_LOG_TATER, NEBlocks.STRIPPED_JUNGLE_LOG_TATER, NEBlocks.STRIPPED_MANGROVE_LOG_TATER, NEBlocks.STRIPPED_OAK_LOG_TATER, NEBlocks.STRIPPED_SPRUCE_LOG_TATER, NEBlocks.STRIPPED_WARPED_STEM_TATER, NEBlocks.WARPED_STEM_TATER, NEBlocks.WARPED_TATER).parent(tenTaters));
        accept(consumer, "zombie_taters", NEBlocks.ZOMBIE_TATER, requiringTatersCollected(NEBlocks.BOULDERING_ZOMBIE_TATER, NEBlocks.DROWNED_TATER, NEBlocks.HUSK_TATER, NEBlocks.LOBBER_ZOMBIE_TATER, NEBlocks.ZOMBIE_TATER, NEBlocks.ZOMBIE_VILLAGER_TATER, NEBlocks.ZOMBIFIED_PIGLIN_TATER).parent(firstTater));

        // Special conditions
        accept(consumer, "wednesday_my_dudes", null, Advancement.Builder.createUntelemetered()
                .display(
                        NEBlocks.WARM_FROG_TATER,
                        Text.translatable("advancements.nucleoid_extras.wednesday_my_dudes.title"),
                        Text.translatable("advancements.nucleoid_extras.wednesday_my_dudes.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        true
                )
                .criterion("wear_cold_frog_tater", NECriteria.WEAR_TATER.create(
                        new WearTaterCriterion.Conditions(getTaterEntry(NEBlocks.COLD_FROG_TATER), Optional.of(4))
                ))
                .criterion("wear_temperate_frog_tater", NECriteria.WEAR_TATER.create(
                        new WearTaterCriterion.Conditions(getTaterEntry(NEBlocks.TEMPERATE_FROG_TATER), Optional.of(4))
                ))
                .criterion("wear_warm_frog_tater", NECriteria.WEAR_TATER.create(
                        new WearTaterCriterion.Conditions(getTaterEntry(NEBlocks.WARM_FROG_TATER), Optional.of(4))
                ))
                .criteriaMerger(CriterionMerger.OR)
                .parent(frogTaters)
        );

        // Based on Minecraft releases
        accept(consumer, "pre_classic_taters", NEBlocks.COBBLESTONE_TATER, requiringTatersCollected(NEBlocks.COBBLESTONE_TATER, NEBlocks.DIRT_TATER, NEBlocks.GRASS_TATER, NEBlocks.OAK_TATER, NEBlocks.STEVE_TATER, NEBlocks.STONE_TATER).parent(firstTater));
        accept(consumer, "classic_taters", NEBlocks.CREEPER_TATER, requiringTatersCollected(NEBlocks.BEDROCK_TATER, NEBlocks.BOOKSHELF_TATER, NEBlocks.BRICK_TATER, NEBlocks.CREEPER_TATER, NEBlocks.GOLD_TATER, NEBlocks.IRON_TATER, NEBlocks.OAK_LOG_TATER, NEBlocks.OBSIDIAN_TATER, NEBlocks.PIG_TATER, NEBlocks.SAND_TATER, NEBlocks.SHEEP_TATER, NEBlocks.SKELETATER, NEBlocks.SPIDER_TATER, NEBlocks.SPONGE_TATER, NEBlocks.TNTATER, NEBlocks.WOOL_TATER, NEBlocks.ZOMBIE_TATER).parent(tenTaters));
        accept(consumer, "indev_taters", NEBlocks.CRAFTING_TATER, requiringTatersCollected(NEBlocks.APPLE_TATER, NEBlocks.CRAFTING_TATER, NEBlocks.DIAMOND_TATER, NEBlocks.FURNACE_TATER, NEBlocks.GOLDEN_APPLE_TATER).parent(firstTater));
        accept(consumer, "alpha_taters", NEBlocks.NETHERRACK_TATER, requiringTatersCollected(NEBlocks.CACTUS_TATER, NEBlocks.CLAY_TATER, NEBlocks.COW_TATER, NEBlocks.GHAST_TATER, NEBlocks.GLOWSTONE_TATER, NEBlocks.ICE_TATER, NEBlocks.JACK_O_TATER, NEBlocks.JUKEBOX_TATER, NEBlocks.NETHERRACK_TATER, NEBlocks.PUMPKIN_TATER, NEBlocks.SLIME_TATER, NEBlocks.SOUL_SAND_TATER, NEBlocks.ZOMBIFIED_PIGLIN_TATER).parent(tenTaters));
        accept(consumer, "beta_taters", NEBlocks.CAKE_TATER, requiringTatersCollected(NEBlocks.BIRCH_LOG_TATER, NEBlocks.CAKE_TATER, NEBlocks.CHARGED_CREEPER_TATER, NEBlocks.LAPIS_TATER, NEBlocks.PISTON_TATER, NEBlocks.SANDSTONE_TATER, NEBlocks.SPRUCE_LOG_TATER, NEBlocks.SQUID_TATER, NEBlocks.STICKY_PISTON_TATER).parent(firstTater));
        accept(consumer, "adventure_taters", NEBlocks.ENDERTATER, requiringTatersCollected(NEBlocks.BLAZE_TATER, NEBlocks.BROWN_MUSHROOM_TATER, NEBlocks.CAVE_SPIDER_TATER, NEBlocks.END_STONE_TATER, NEBlocks.ENDERTATER, NEBlocks.EYE_OF_ENDER_TATER, NEBlocks.MAGMA_CUBE_TATER, NEBlocks.MELON_TATER, NEBlocks.MUSHROOM_STEM_TATER, NEBlocks.MYCELIUM_TATER, NEBlocks.NETHER_BRICK_TATER, NEBlocks.RED_MOOSHROOM_TATER, NEBlocks.RED_MUSHROOM_TATER, NEBlocks.SNOWMAN_TATER, NEBlocks.STONE_BRICK_TATER, NEBlocks.VILLAGER_TATER).parent(tenTaters));
        accept(consumer, "1_2_taters", NEBlocks.JUNGLE_TATER, requiringTatersCollected(NEBlocks.JUNGLE_TATER, NEBlocks.JUNGLE_LOG_TATER, NEBlocks.SPRUCE_TATER, NEBlocks.BIRCH_TATER).parent(firstTater));
        accept(consumer, "1_3_taters", NEBlocks.EMERALD_TATER, requiringTatersCollected(NEBlocks.BIRCH_TATER, NEBlocks.COCOA_TATER, NEBlocks.EMERALD_TATER, NEBlocks.SPRUCE_TATER).parent(firstTater));
        accept(consumer, "pretty_scary_taters", NEBlocks.WITHER_TATER, requiringTatersCollected(NEBlocks.BEACON_TATER, NEBlocks.FLOWER_POT_TATER, NEBlocks.SHIELDED_WITHER_TATER, NEBlocks.WITCH_TATER, NEBlocks.WITHER_SKELETATER, NEBlocks.WITHER_TATER, NEBlocks.ZOMBIE_VILLAGER_TATER).parent(firstTater));
        accept(consumer, "redstone_taters", NEBlocks.REDSTONE_TATER, requiringTatersCollected(NEBlocks.DAYLIGHT_DETECTOR_TATER, NEBlocks.QUARTZ_TATER, NEBlocks.REDSTONE_TATER).parent(firstTater));
        accept(consumer, "horse_taters", NEBlocks.HAY_TATER, requiringTatersCollected(NEBlocks.COAL_TATER, NEBlocks.HAY_TATER, NEBlocks.TERRACOTTA_TATER).parent(firstTater));
        accept(consumer, "changed_the_world_taters", NEBlocks.DARK_OAK_TATER, requiringTatersCollected(NEBlocks.ACACIA_LOG_TATER, NEBlocks.ACACIA_TATER, NEBlocks.DARK_OAK_LOG_TATER, NEBlocks.DARK_OAK_TATER, NEBlocks.PACKED_ICE_TATER, NEBlocks.PODZOL_TATER, NEBlocks.PUFFERTATER, NEBlocks.RED_SAND_TATER).parent(firstTater));
        accept(consumer, "bountiful_taters", NEBlocks.GRANITE_TATER, requiringTatersCollected(NEBlocks.ALEX_TATER, NEBlocks.ANDESITE_TATER, NEBlocks.DARK_PRISMARINE_TATER, NEBlocks.DIORITE_TATER, NEBlocks.ELDER_GUARDIAN_TATER, NEBlocks.GRANITE_TATER, NEBlocks.GUARDIAN_TATER, NEBlocks.INVERTED_DAYLIGHT_DETECTOR_TATER, NEBlocks.PRISMARINE_BRICK_TATER, NEBlocks.PRISMARINE_TATER, NEBlocks.RED_SANDSTONE_TATER, NEBlocks.SEA_LANTERN_TATER, NEBlocks.SLIME_TATER, NEBlocks.SPONGE_TATER).parent(tenTaters));
        accept(consumer, "combat_taters", NEBlocks.PURPUR_TATER, requiringTatersCollected(NEBlocks.CHORUS_TATER, NEBlocks.END_STONE_BRICK_TATER, NEBlocks.PURPUR_TATER).parent(firstTater));
        accept(consumer, "frostburn_taters", NEBlocks.POLAR_BEAR_TATER, requiringTatersCollected(NEBlocks.BONE_TATER, NEBlocks.HUSK_TATER, NEBlocks.NETHER_WART_TATER, NEBlocks.POLAR_BEAR_TATER, NEBlocks.RED_NETHER_BRICK_TATER, NEBlocks.STRAY_TATER).parent(firstTater));
        accept(consumer, "exploration_taters", NEBlocks.VEX_TATER, requiringTatersCollected(NEBlocks.ILLAGER_TATER, NEBlocks.TATER_OF_UNDYING, NEBlocks.VEX_TATER, NEBlocks.VINDITATER).parent(firstTater));
        // No taters for new blocks in Minecraft 1.12
        accept(consumer, "aquatic_taters", NEBlocks.PRISMARINE_TATER, requiringTatersCollected(NEBlocks.BLUE_ICE_TATER, NEBlocks.BRAIN_CORAL_TATER, NEBlocks.BUBBLE_CORAL_TATER, NEBlocks.CONDUIT_TATER, NEBlocks.DEAD_BRAIN_CORAL_TATER, NEBlocks.DEAD_BUBBLE_CORAL_TATER, NEBlocks.DEAD_FIRE_CORAL_TATER, NEBlocks.DEAD_HORN_CORAL_TATER, NEBlocks.DEAD_TUBE_CORAL_TATER, NEBlocks.DRIED_KELP_TATER, NEBlocks.DROWNED_TATER, NEBlocks.FIRE_CORAL_TATER, NEBlocks.HORN_CORAL_TATER, NEBlocks.PUFFERTATER, NEBlocks.SEA_PICKLE_TATER, NEBlocks.SMOOTH_STONE_TATER, NEBlocks.STRIPPED_ACACIA_LOG_TATER, NEBlocks.STRIPPED_BIRCH_LOG_TATER, NEBlocks.STRIPPED_DARK_OAK_LOG_TATER, NEBlocks.STRIPPED_JUNGLE_LOG_TATER, NEBlocks.STRIPPED_OAK_LOG_TATER, NEBlocks.STRIPPED_SPRUCE_LOG_TATER, NEBlocks.TUBE_CORAL_TATER, NEBlocks.TURTLE_EGG_TATER).parent(coralTaters));
        accept(consumer, "village_and_pillage_taters", NEBlocks.ILLAGER_TATER, requiringTatersCollected(NEBlocks.BAMBOO_TATER, NEBlocks.BELL_TATER, NEBlocks.BROWN_MOOSHROOM_TATER, NEBlocks.FOX_TATER, NEBlocks.ILLAGER_TATER, NEBlocks.LANTERN_TATER, NEBlocks.SNOW_FOX_TATER, NEBlocks.WANDERING_TRADER_TATER).parent(firstTater));
        accept(consumer, "buzzy_taters", NEBlocks.BEE_TATER, requiringTatersCollected(NEBlocks.BEE_NEST_TATER, NEBlocks.BEE_TATER, NEBlocks.BEEHIVE_TATER, NEBlocks.HONEY_TATER, NEBlocks.HONEYCOMB_TATER).parent(firstTater));
        accept(consumer, "nether_taters", NEBlocks.WARPED_NYLIUM_TATER, requiringTatersCollected(NEBlocks.BASALT_TATER, NEBlocks.BLACKSTONE_TATER, NEBlocks.COLD_STRIDER_TATER, NEBlocks.CRIMSON_NYLIUM_TATER, NEBlocks.CRIMSON_STEM_TATER, NEBlocks.CRIMSON_TATER, NEBlocks.CRYING_OBSIDIAN_TATER, NEBlocks.GILDED_BLACKSTONE_TATER, NEBlocks.NETHERITE_TATER, NEBlocks.PIGLIN_TATER, NEBlocks.SHROOMLIGHT_TATER, NEBlocks.SOUL_LANTERN_TATER, NEBlocks.SOUL_SOIL_TATER, NEBlocks.STRIDER_TATER, NEBlocks.STRIPPED_CRIMSON_STEM_TATER, NEBlocks.STRIPPED_WARPED_STEM_TATER, NEBlocks.WARPED_NYLIUM_TATER, NEBlocks.WARPED_STEM_TATER, NEBlocks.WARPED_TATER, NEBlocks.WARPED_WART_TATER, NEBlocks.ZOMBIFIED_PIGLIN_TATER).parent(tenTaters));
        accept(consumer, "caves_and_cliffs_taters", NEBlocks.AZALEA_TATER, requiringTatersCollected(NEBlocks.AMETHYST_TATER, NEBlocks.AZALEA_TATER, NEBlocks.BLUE_AXOLOTL_TATER, NEBlocks.CALCITE_TATER, NEBlocks.COBBLED_DEEPSLATE_TATER, NEBlocks.COPPER_TATER, NEBlocks.CYAN_AXOLOTL_TATER, NEBlocks.DEEPSLATE_BRICK_TATER, NEBlocks.DEEPSLATE_TATER, NEBlocks.DRIPSTONE_TATER, NEBlocks.EXPOSED_COPPER_TATER, NEBlocks.FLOWERING_AZALEA_TATER, NEBlocks.GLOW_SQUID_TATER, NEBlocks.GOLD_AXOLOTL_TATER, NEBlocks.LUCY_AXOLOTL_TATER, NEBlocks.OXIDIZED_COPPER_TATER, NEBlocks.RAW_COPPER_TATER, NEBlocks.RAW_GOLD_TATER, NEBlocks.RAW_IRON_TATER, NEBlocks.TUFF_TATER, NEBlocks.WEATHERED_COPPER_TATER, NEBlocks.WILD_AXOLOTL_TATER).parent(axolotlTaters));
        accept(consumer, "wild_taters", NEBlocks.WARDEN_TATER, requiringTatersCollected(NEBlocks.ALLAY_TATER, NEBlocks.COLD_FROG_TATER, NEBlocks.MANGROVE_LOG_TATER, NEBlocks.MANGROVE_TATER, NEBlocks.MUD_BRICK_TATER, NEBlocks.MUD_TATER, NEBlocks.OCHRE_FROGLIGHT_TATER, NEBlocks.PACKED_MUD_TATER, NEBlocks.PEARLESCENT_FROGLIGHT_TATER, NEBlocks.SCULK_TATER, NEBlocks.STRIPPED_MANGROVE_LOG_TATER, NEBlocks.TEMPERATE_FROG_TATER, NEBlocks.VERDANT_FROGLIGHT_TATER, NEBlocks.WARDEN_TATER, NEBlocks.WARM_FROG_TATER).parent(froglightTaters));
    }

    private static Advancement.Builder requiringTatersCollected(int count) {
        return requiringTatersCollected(new TaterCount.Value(count));
    }

    private static Advancement.Builder requiringTatersCollected(TaterCount count) {
        var builder = Advancement.Builder.createUntelemetered();

        var name = "get_" + count.count(null) + "_tater" + (count.count(null) == 1 ? "" : "s");
        var conditions = new TaterCollectedCriterion.Conditions(Optional.empty(), Optional.of(count));

        builder.criterion(name, NECriteria.TATER_COLLECTED.create(conditions));

        return builder;
    }

    private static Advancement.Builder requiringTatersCollected(Block... taters) {
        var builder = Advancement.Builder.createUntelemetered();

        for (Block tater : taters) {
            var id = Registries.BLOCK.getId(tater);
            var name = "get_" + id.getPath();

            var conditions = new TaterCollectedCriterion.Conditions(getTaterEntry(tater), Optional.empty());

            builder.criterion(name, NECriteria.TATER_COLLECTED.create(conditions));
        }

        return builder;
    }

    private static AdvancementEntry accept(Consumer<AdvancementEntry> consumer, String path, ItemConvertible icon, Advancement.Builder builder) {
        return accept(consumer, path, icon, AdvancementFrame.TASK, builder);
    }

    private static AdvancementEntry accept(Consumer<AdvancementEntry> consumer, String path, ItemConvertible icon, AdvancementFrame frame, Advancement.Builder builder) {
        if (icon != null) {
            builder.display(
                    icon,
                    Text.translatable("advancements.nucleoid_extras." + path + ".title"),
                    Text.translatable("advancements.nucleoid_extras." + path + ".description"),
                    null,
                    frame,
                    true,
                    true,
                    false
            );
        }

        var id = NucleoidExtras.identifier("taters/" + path);
        var advancement = builder.build(id);

        consumer.accept(advancement);
        return advancement;
    }

    private static Optional<RegistryEntry<Block>> getTaterEntry(Block block) {
        if (block instanceof TinyPotatoBlock tater) {
            return Optional.of(tater.getRegistryEntry());
        }

        throw new IllegalArgumentException("Not a tater: " + block);
    }
}
