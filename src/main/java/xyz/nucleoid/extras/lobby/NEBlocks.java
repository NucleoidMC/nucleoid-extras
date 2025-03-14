package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.block.*;
import xyz.nucleoid.extras.lobby.block.tater.*;

import java.util.function.Function;

public class NEBlocks {
    public static final Block NUCLEOID_LOGO = registerTaterBlock("nucleoid_logo", ParticleTypes.GLOW_SQUID_INK, "bac7400dfcb9a387361a3ad7c296943eb841a9bda13ad89558e2d6efebf167bc");

    public static final Block END_PORTAL = registerSimple("end_portal", Blocks.END_PORTAL);
    public static final Block END_GATEWAY = register("end_gateway", AbstractBlock.Settings.create().pistonBehavior(PistonBehavior.BLOCK).strength(100).noCollision(), settings -> new VirtualEndGatewayBlock(settings));
    public static final Block SAFE_TNT = registerSimple("safe_tnt", Blocks.TNT);

    public static final Block BLACK_CONCRETE_POWDER = registerSimple("black_concrete_powder", Blocks.BLACK_CONCRETE_POWDER);
    public static final Block BLUE_CONCRETE_POWDER = registerSimple("blue_concrete_powder", Blocks.BLUE_CONCRETE_POWDER);
    public static final Block BROWN_CONCRETE_POWDER = registerSimple("brown_concrete_powder", Blocks.BROWN_CONCRETE_POWDER);
    public static final Block CYAN_CONCRETE_POWDER = registerSimple("cyan_concrete_powder", Blocks.CYAN_CONCRETE_POWDER);
    public static final Block GREEN_CONCRETE_POWDER = registerSimple("green_concrete_powder", Blocks.GREEN_CONCRETE_POWDER);
    public static final Block GRAY_CONCRETE_POWDER = registerSimple("gray_concrete_powder", Blocks.GRAY_CONCRETE_POWDER);
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = registerSimple("light_blue_concrete_powder", Blocks.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = registerSimple("light_gray_concrete_powder", Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Block LIME_CONCRETE_POWDER = registerSimple("lime_concrete_powder", Blocks.LIME_CONCRETE_POWDER);
    public static final Block MAGENTA_CONCRETE_POWDER = registerSimple("magenta_concrete_powder", Blocks.MAGENTA_CONCRETE_POWDER);
    public static final Block ORANGE_CONCRETE_POWDER = registerSimple("orange_concrete_powder", Blocks.ORANGE_CONCRETE_POWDER);
    public static final Block PINK_CONCRETE_POWDER = registerSimple("pink_concrete_powder", Blocks.PINK_CONCRETE_POWDER);
    public static final Block PURPLE_CONCRETE_POWDER = registerSimple("purple_concrete_powder", Blocks.PURPLE_CONCRETE_POWDER);
    public static final Block RED_CONCRETE_POWDER = registerSimple("red_concrete_powder", Blocks.RED_CONCRETE_POWDER);
    public static final Block WHITE_CONCRETE_POWDER = registerSimple("white_concrete_powder", Blocks.WHITE_CONCRETE_POWDER);
    public static final Block YELLOW_CONCRETE_POWDER = registerSimple("yellow_concrete_powder", Blocks.YELLOW_CONCRETE_POWDER);

    public static final Block GOLD_LAUNCH_PAD = register("gold_launch_pad", AbstractBlock.Settings.copy(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).strength(100).noCollision(), settings -> new LaunchPadBlock(settings, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState()));
    public static final Block IRON_LAUNCH_PAD = register("iron_launch_pad", AbstractBlock.Settings.copy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE).strength(100).noCollision(), settings -> new LaunchPadBlock(settings, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState()));

    public static final Block CONTRIBUTOR_STATUE = register("contributor_statue", AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE).strength(100), settings -> new ContributorStatueBlock(settings));

    public static final Block INFINITE_DISPENSER = register("infinite_dispenser", AbstractBlock.Settings.copy(Blocks.DISPENSER).strength(100), settings -> new InfiniteDispenserBlock(settings));
    public static final Block INFINITE_DROPPER = register("infinite_dropper", AbstractBlock.Settings.copy(Blocks.DROPPER).strength(100), settings -> new InfiniteDropperBlock(settings));

    public static final Block SNAKE_BLOCK = register("snake_block", AbstractBlock.Settings.copy(Blocks.LIME_CONCRETE).strength(100), settings -> new SnakeBlock(settings, Blocks.LIME_CONCRETE.getDefaultState(), 8, 7));
    public static final Block FAST_SNAKE_BLOCK = register("fast_snake_block", AbstractBlock.Settings.copy(Blocks.LIGHT_BLUE_CONCRETE).strength(100), settings -> new SnakeBlock(settings, Blocks.LIGHT_BLUE_CONCRETE.getDefaultState(), 4, 7));

    public static final Block TRANSIENT_IRON_DOOR = register("transient_iron_door", AbstractBlock.Settings.copy(Blocks.IRON_DOOR), settings -> new TransientDoorBlock(Blocks.IRON_DOOR, settings));
    public static final Block TRANSIENT_OAK_DOOR = register("transient_oak_door", AbstractBlock.Settings.copy(Blocks.OAK_DOOR), settings -> new TransientDoorBlock(Blocks.OAK_DOOR, settings));
    public static final Block TRANSIENT_SPRUCE_DOOR = register("transient_spruce_door", AbstractBlock.Settings.copy(Blocks.SPRUCE_DOOR), settings -> new TransientDoorBlock(Blocks.SPRUCE_DOOR, settings));
    public static final Block TRANSIENT_BIRCH_DOOR = register("transient_birch_door", AbstractBlock.Settings.copy(Blocks.BIRCH_DOOR), settings -> new TransientDoorBlock(Blocks.BIRCH_DOOR, settings));
    public static final Block TRANSIENT_JUNGLE_DOOR = register("transient_jungle_door", AbstractBlock.Settings.copy(Blocks.JUNGLE_DOOR), settings -> new TransientDoorBlock(Blocks.JUNGLE_DOOR, settings));
    public static final Block TRANSIENT_ACACIA_DOOR = register("transient_acacia_door", AbstractBlock.Settings.copy(Blocks.ACACIA_DOOR), settings -> new TransientDoorBlock(Blocks.ACACIA_DOOR, settings));
    public static final Block TRANSIENT_CHERRY_DOOR = register("transient_cherry_door", AbstractBlock.Settings.copy(Blocks.CHERRY_DOOR), settings -> new TransientDoorBlock(Blocks.CHERRY_DOOR, settings));
    public static final Block TRANSIENT_DARK_OAK_DOOR = register("transient_dark_oak_door", AbstractBlock.Settings.copy(Blocks.DARK_OAK_DOOR), settings -> new TransientDoorBlock(Blocks.DARK_OAK_DOOR, settings));
    public static final Block TRANSIENT_MANGROVE_DOOR = register("transient_mangrove_door", AbstractBlock.Settings.copy(Blocks.MANGROVE_DOOR), settings -> new TransientDoorBlock(Blocks.MANGROVE_DOOR, settings));
    public static final Block TRANSIENT_PALE_OAK_DOOR = register("transient_pale_oak_door", AbstractBlock.Settings.copy(Blocks.PALE_OAK_DOOR), settings -> new TransientDoorBlock(Blocks.PALE_OAK_DOOR, settings));
    public static final Block TRANSIENT_BAMBOO_DOOR = register("transient_bamboo_door", AbstractBlock.Settings.copy(Blocks.BAMBOO_DOOR), settings -> new TransientDoorBlock(Blocks.BAMBOO_DOOR, settings));
    public static final Block TRANSIENT_CRIMSON_DOOR = register("transient_crimson_door", AbstractBlock.Settings.copy(Blocks.CRIMSON_DOOR), settings -> new TransientDoorBlock(Blocks.CRIMSON_DOOR, settings));
    public static final Block TRANSIENT_WARPED_DOOR = register("transient_warped_door", AbstractBlock.Settings.copy(Blocks.WARPED_DOOR), settings -> new TransientDoorBlock(Blocks.WARPED_DOOR, settings));
    public static final Block TRANSIENT_COPPER_DOOR = register("transient_copper_door", AbstractBlock.Settings.copy(Blocks.COPPER_DOOR), settings -> new TransientOxidizableDoorBlock(Blocks.COPPER_DOOR, settings));
    public static final Block TRANSIENT_EXPOSED_COPPER_DOOR = register("transient_exposed_copper_door", AbstractBlock.Settings.copy(Blocks.EXPOSED_COPPER_DOOR), settings -> new TransientOxidizableDoorBlock(Blocks.EXPOSED_COPPER_DOOR, settings));
    public static final Block TRANSIENT_WEATHERED_COPPER_DOOR = register("transient_weathered_copper_door", AbstractBlock.Settings.copy(Blocks.WEATHERED_COPPER_DOOR), settings -> new TransientOxidizableDoorBlock(Blocks.WEATHERED_COPPER_DOOR, settings));
    public static final Block TRANSIENT_OXIDIZED_COPPER_DOOR = register("transient_oxidized_copper_door", AbstractBlock.Settings.copy(Blocks.OXIDIZED_COPPER_DOOR), settings -> new TransientOxidizableDoorBlock(Blocks.OXIDIZED_COPPER_DOOR, settings));
    public static final Block TRANSIENT_WAXED_COPPER_DOOR = register("transient_waxed_copper_door", AbstractBlock.Settings.copy(Blocks.WAXED_COPPER_DOOR), settings -> new TransientDoorBlock(Blocks.WAXED_COPPER_DOOR, settings));
    public static final Block TRANSIENT_WAXED_EXPOSED_COPPER_DOOR = register("transient_waxed_exposed_copper_door", AbstractBlock.Settings.copy(Blocks.WAXED_EXPOSED_COPPER_DOOR), settings -> new TransientDoorBlock(Blocks.WAXED_EXPOSED_COPPER_DOOR, settings));
    public static final Block TRANSIENT_WAXED_WEATHERED_COPPER_DOOR = register("transient_waxed_weathered_copper_door", AbstractBlock.Settings.copy(Blocks.WAXED_WEATHERED_COPPER_DOOR), settings -> new TransientDoorBlock(Blocks.WAXED_WEATHERED_COPPER_DOOR, settings));
    public static final Block TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR = register("transient_waxed_oxidized_copper_door", AbstractBlock.Settings.copy(Blocks.WAXED_OXIDIZED_COPPER_DOOR), settings -> new TransientDoorBlock(Blocks.WAXED_OXIDIZED_COPPER_DOOR, settings));

    public static final Block NUCLE_PAST_LOGO = registerTaterBlock("nucle_past_logo", new DustParticleEffect(0x52C471, 1), "65ed3e4d6ec42bd84d2b5e452087d454aac141a978540f6d200bd8aa863d4db8");

    public static final Block TINY_POTATO = registerTaterBlock("tiny_potato", ParticleTypes.HEART, "573514a23245f15dbad5fb4e622163020864cce4c15d56de3adb90fa5a7137fd");
    public static final Block BOTANICAL_TINY_POTATO = registerBotanicTaterBlock("botanical_potato", ParticleTypes.HEART,
        "39e878c52870c640b5985c67df70059120b61b26c77a5cf86042c04c13477d7b",
        "582f367eabffc9ecd8ab870c9f5f5c8b43215d5eb922cfb193aed70fcf694e92"
    );
    public static final Block IRRITATER = registerTaterBlock("irritater", ParticleTypes.ANGRY_VILLAGER, "14b2cbfe1fd4d3123461081ad460acb6c0345bed3f3ce96d475b5f58f7b9030b");
    public static final Block SAD_TATER = registerTaterBlock("sad_tater", ParticleTypes.FALLING_WATER, "7915f5ab6a3af5fd8e043bc98a5466acfc5d57c30dc9a1d2e4a32f7bfa1d35bf");
    public static final Block FLOWERING_AZALEA_TATER = registerTaterBlock("flowering_azalea_tater", Blocks.FLOWERING_AZALEA_LEAVES, "ab6c05d3be9369c69984513f281932622bca807008def997222a6d4f8cb71d83");
    public static final Block STONE_TATER = registerTaterBlock("stone_tater", Blocks.STONE, "62e56013a5f6399b5fb91bb620dc240bb04eb7d1d71e0811853058b6d5ed6291");
    public static final Block CALCITE_TATER = registerTaterBlock("calcite_tater", Blocks.CALCITE, "ed8d2215b560c6755a1224d33041814c5ede9638ccb480a92baf79390080e687");
    public static final Block TUFF_TATER = registerTaterBlock("tuff_tater", Blocks.TUFF, "8612a9dfbe1906113c8ff80ba724af1dcf8ad68f95cf15f8a29931b82db5bada");
    public static final Block BASALT_TATER = registerTaterBlock("basalt_tater", ParticleTypes.WHITE_ASH, "c3038dfba5fc6877891d5a6c904f3e90c5ce8d76b630314a1b851b3d67e79fea");
    public static final Block DRIPSTONE_TATER = registerTaterBlock("dripstone_tater", Blocks.DRIPSTONE_BLOCK, "906ea8dce1162e46353d9369fb9313e8ec2a63e64eaa617b02fe5f39073d06d8");
    public static final Block AMETHYST_TATER = registerTaterBlock("amethyst_tater", Blocks.AMETHYST_BLOCK, "a0972dbf71067c17a01a6130586a8f0af80b267c4c1c7407eee059214a02da58");
    public static final Block PACKED_ICE_TATER = registerTaterBlock("packed_ice_tater", Blocks.PACKED_ICE, "5ee41f4bbc38815b497ab0bff1a981a7cd2121b539661662df4f2851500737e3");
    public static final Block BLUE_ICE_TATER = registerTaterBlock("blue_ice_tater", Blocks.BLUE_ICE, "fb00ec6cb1928448fd72add4352643a7d04dc1fb54cbb37d8094844a84646830");
    public static final Block FLAME_TATER = registerTaterBlock("flame_tater", ParticleTypes.FLAME, "969a0e61b1ebf03a23a6e5885d133c0055f7755c3b63e3fb2ac475aaf4d87467");
    public static final Block PUZZLE_CUBE_TATER = registerTaterBlock("puzzle_cube_tater", ParticleTypes.FIREWORK, "41f8da5c342e799bfae154c16627b6190923eae27735ef7ffbdeb1a121c8811b");
    public static final Block LUCKY_TATER = registerLuckyTaterBlock("lucky_tater", "7417598f8d30dd3582ce723a1303abeeca9ac6a96438967b7f4c043fe3562ebb", "a590c5d7d05cd4ad1747b7b4e265dc97a07b054175e1f25b488c2de021075329");
    public static final Block CRATE_TATER = registerTaterBlock("crate_tater", ParticleTypes.WAX_ON, "5b7d34f16ef9a361964ce7405b6b463f60cc214bdb625e089869c50f5332f61e");
    public static final Block TATER_OF_UNDYING = registerTaterBlock("tater_of_undying", ParticleTypes.TOTEM_OF_UNDYING, "b526d93147825e2db444aaf4a9464b61ad4e4defb0adf944a2275543efc9192a");
    public static final Block CRYING_OBSIDIAN_TATER = registerTaterBlock("crying_obsidian_tater", ParticleTypes.DRIPPING_OBSIDIAN_TEAR, "70d151a2da83b4a0ec73be08a42c4bc8964ff1d3ae2106cc29b9b79cfccb8b9f");
    public static final Block FLIPPED_TATER = registerTaterBlock("flipped_tater", ParticleTypes.DAMAGE_INDICATOR, "9c1e33c4b7e6cb58e699aeb7ae412329f35cb443e50743c8896ed36dfb6a3588");
    public static final Block BACKWARD_TATER = registerTaterBlock("backward_tater", ParticleTypes.HEART, "c3d2eefca5fa2e0cc710fe067f4a7114df0f430eeaaa1d9c373e4c91c9ed0ea4");
    public static final Block UPWARD_TATER = registerTaterBlock("upward_tater", ParticleTypes.HEART, "60860143fea936066220eae3a31cdfe5aa9b4e525e194aa965c02272a01cb5c8");
    public static final Block SANTA_HAT_TATER = registerTaterBlock("santa_hat_tater", ParticleTypes.SNOWFLAKE, "7609f3510b29bfa1504b7d3d53f466e7628a1cebd102573601db33e1ced88c4e");

    public static final Block SKELETATER = registerTaterBlock("skeletater", Blocks.BONE_BLOCK, "f472e2fc36c7d8ef7cff8cfba71e7e238f89de01e8d44c21703f1ac6d2c47f1");
    public static final Block WITHER_SKELETATER = registerTaterBlock("wither_skeletater", Blocks.SOUL_SAND, "2c739e03aaf98f1e972e23122423f82164bceadce39221fa7f6909d90eb05223");
    public static final Block ZOMBIE_TATER = registerTaterBlock("zombie_tater", Items.ROTTEN_FLESH, "9495d6aa5ef185062e8d9d1ba1721d5144e24155244f53ea4129038f977a0735");
    public static final Block CREEPER_TATER = registerTaterBlock("creeper_tater", ParticleTypes.EXPLOSION, "81428b1867a1b25cfce24ae821986653b1eb9dc253630ab82b695c2cbb6d1e52", 10);
    public static final Block STEVE_TATER = registerTaterBlock("steve_tater", ParticleTypes.HAPPY_VILLAGER, "ce7cc05bee993cd701a742415b09156381a54aaa7ad5971941722e32dd6ef3f2");
    public static final Block ALEX_TATER = registerTaterBlock("alex_tater", ParticleTypes.HAPPY_VILLAGER, "e1f7dc909b2d6c679ffb4c69c8d7c8ed39cf47a83330113f8fe45ac4b0a483c8");

    public static final Block TRANS_TATER = registerColorPatternTaterBlock("trans_tater", new int[]{
        0xEE90AD, // pink
        0x3CB0DA, // blue
        0xCFD5D6, // white
    }, "f77dbc809b254449023fac0dd4e0d9100b5c4407748be089f0e00c7ef7ab764");
    public static final Block ASEXUAL_TATER = registerColorPatternTaterBlock("asexual_tater", new int[]{
        0x16161B, // black
        0x3F4548, // gray
        0xCFD5D6, // white
        0x7B2BAD, // purple
    }, "3902887dc55d4f736d0b566ad812f256113aaa4a318ffb865623fb5a677aef32");
    public static final Block BI_TATER = registerColorPatternTaterBlock("bi_tater", new int[]{
        0xBE46B5, // pink
        0x7B2BAD, // purple
        0x353A9E, // blue
    }, "4526a72ca5be42920cd310280c03e2c9e9a70c55aa9cc1a0c48396d556f1c75d");
    public static final Block GAY_TATER = registerColorPatternTaterBlock("gay_tater", new int[]{
        0xA12823, // red
        0xF17716, // orange
        0xF9C629, // yellow
        0x556E1C, // green
        0x353A9E, // blue
        0x7B2BAD, // purple
    }, "f9f446f29396ff444d0ef4f53a70c28afb69e5d1da037c03c277d23917dacded");
    public static final Block LESBIAN_TATER = registerColorPatternTaterBlock("lesbian_tater", new int[]{
        0xA12823, // red
        0xF17716, // orange
        0xEAEDED, // white
        0xEE90AD, // pink
        0xBE46B5, // magenta
    }, "44492740f40c19c3e52871cdf6cbd585e980fc7b50cb0fc949bfbe44032a7db7");
    public static final Block NONBINARY_TATER = registerColorPatternTaterBlock("nonbinary_tater", new int[]{
        0xF9C629, // yellow
        0x16161B, // black
        0xCFD5D6, // white
        0x7B2BAD, // purple
    }, "10854e473bc7a0a6956cb12df8026de9fc00fae40c0502a3182908bbb50c9aa5");
    public static final Block PAN_TATER = registerColorPatternTaterBlock("pan_tater", new int[]{
        0xFA318C, // pink
        0xFDD73B, // yellow
        0x2394F9, // blue
    }, "3f761be18f070a016e4f61d37ec13b23032a552dcdb70a67f855c3ab2fae54e0");
    public static final Block GENDERFLUID_TATER = registerColorPatternTaterBlock("genderfluid_tater", new int[]{
        0xBE46B5, // pink
        0xCFD5D6, // white
        0x7B2BAD, // purple
        0x16161B, // black
        0x2394F9, // blue
    }, "ba066cdd8d48501eb51eea1e3e417c25ef51a04284714baad5ab5de5cd4221b8");
    public static final Block DEMISEXUAL_TATER = registerColorPatternTaterBlock("demisexual_tater", new int[]{
        0x16161B, // black
        0xCFD5D6, // white
        0x7B2BAD, // purple
        0x3F4548, // gray
    }, "32b7cd2c5d70cab476ce951e2c520c9b3579250ad900164d6c2321c7f43d6dc7");

    public static final Block WARDEN_TATER = registerWardenTaterBlock("warden_tater", "52e411aa1501c72d99d738cb38e250a395c6604b8bccc9f29d7f26e9cacd8d6f");
    public static final Block CREAKING_TATER = registerGlowingLayerTaterBlock("creaking_tater", new BlockStateParticleEffect(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.getDefaultState()), "ba2adfd9b68769ba7ecd4b35d904c7756d1e232e7f2b9cbcbdf49f6a34162e54", GlowingLayerTaterBlock.Pixel.CREAKING);
    public static final Block VIRAL_TATER = registerTaterBlock("viral_tater", ParticleTypes.SCRAPE, "b12f770c4542c9f26ba03aaee686e0946698d394a8e745d3eac6013383dcff29");
    public static final Block DICE_TATER = registerDiceTaterBlock("dice_tater");
    public static final Block TATEROID = registerTateroidBlock("tateroid", SoundEvents.BLOCK_NOTE_BLOCK_BELL, -1, "8d531d40d09efd3a9a585b55e66a9a6f04c73af84d94d7c565549bf27b8b26bd");
    public static final Block RED_TATEROID = registerTateroidBlock("red_tateroid", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, 7 / 24d, "2be51b227360ab65776725a91cded84b56f6920eec0d6fb5a57d5f1ada147aa6");
    public static final Block ORANGE_TATEROID = registerTateroidBlock("orange_tateroid", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, 4 / 24d, "c5362e308822cf1c436a4ba6d0c3976139c98621c7aa2a96be99c73e97708efc");
    public static final Block YELLOW_TATEROID = registerTateroidBlock("yellow_tateroid", SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 2.5 / 24d, "fef74a6c7cb45d3c4bae134e6ec41fd7517f7eabe2c74dc76a51b39c63c38bc2");
    public static final Block GREEN_TATEROID = registerTateroidBlock("green_tateroid", SoundEvents.BLOCK_NOTE_BLOCK_BIT, 21 / 24d, "57bb692499560f0393314a9f1ec11425b360e43c1ddb560de261cd04b8cc8e69");
    public static final Block BLUE_TATEROID = registerTateroidBlock("blue_tateroid", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, 17 / 24d, "89ad5aecfb9ab6f36261e0c462acecf2078e7e575d9373bacc0503224c44250e");
    public static final Block PURPLE_TATEROID = registerTateroidBlock("purple_tateroid", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, 11 / 24d, "d16a37512cb7ca372af5f37f9bd95d4603c4fa44be4143fb26aaa324e681c9b0");

    public static final Block WHITE_TATER = registerColorTaterBlock("white_tater", DyeColor.WHITE, "73dab052d33ee467ba7fac9aa0e316db962e3e7ac6dbbff236667439e340392c");
    public static final Block ORANGE_TATER = registerColorTaterBlock("orange_tater", DyeColor.ORANGE, "75b88126dbd4e860608965c044d0060ac03c26ebea1b652643fe03734ea1b12b");
    public static final Block MAGENTA_TATER = registerColorTaterBlock("magenta_tater", DyeColor.MAGENTA, "bef4cf40f02bc129e34f660ce3923387894b8f0814fc58c09726629fa7b1db64");
    public static final Block LIGHT_BLUE_TATER = registerColorTaterBlock("light_blue_tater", DyeColor.LIGHT_BLUE, "e6f7e4641fa4ee4ba926ebae4669f22f276051b3629f8d89ce015a95c1137fb2");
    public static final Block YELLOW_TATER = registerColorTaterBlock("yellow_tater", DyeColor.YELLOW, "5956df3e88f01cdd26d1caa07fab723c3a3db319ba167a3bed287d461635f1b9");
    public static final Block LIME_TATER = registerColorTaterBlock("lime_tater", DyeColor.LIME, "16671f4c7ce8e46099f367fc05c5b61089c887f4145aff2077a1a7d3631dd063");
    public static final Block PINK_TATER = registerColorTaterBlock("pink_tater", DyeColor.PINK, "b3efebba4906f9f260aae83fee73012370521726fadf0101498197d7143a64df");
    public static final Block GRAY_TATER = registerColorTaterBlock("gray_tater", DyeColor.GRAY, "7e491b6282eca12c9edcddfb3baf6e5fb0549c89d39088567415616c92dfd5f0");
    public static final Block LIGHT_GRAY_TATER = registerColorTaterBlock("light_gray_tater", DyeColor.LIGHT_GRAY, "b5df73cc026043d09c54cb15d0923e314503e1d513026b59a5317b31da4c5289");
    public static final Block CYAN_TATER = registerColorTaterBlock("cyan_tater", DyeColor.CYAN, "4eb39032053a3f2e5bd1d19bfacd25b524b19cd0e70a5a92a61ac884904ecb54");
    public static final Block PURPLE_TATER = registerColorTaterBlock("purple_tater", DyeColor.PURPLE, "ab69ffa68135001e8714c78617e5a1a0177827cbbd866eb984f524f023f87fe5");
    public static final Block BLUE_TATER = registerColorTaterBlock("blue_tater", DyeColor.BLUE, "618be72e94291de1cc3e3d2e2fa8bbd79422cdcb0cb70cedde7dc9c1bbef5fb5");
    public static final Block BROWN_TATER = registerColorTaterBlock("brown_tater", DyeColor.BROWN, "df942f7c24f4c10353da974a938ad75f9a7ccdca232a99de870c5691d75fd70c");
    public static final Block GREEN_TATER = registerColorTaterBlock("green_tater", DyeColor.GREEN, "fab98a4c69e817771fec17c08c7e031952c05a52015dcecb09c93576745c71e2");
    public static final Block RED_TATER = registerColorTaterBlock("red_tater", DyeColor.RED, "51e4bf6f7a029567d598fff73c3c76e0cdea956a7fff5ea7279ea4bf40c968c2");
    public static final Block BLACK_TATER = registerColorTaterBlock("black_tater", DyeColor.BLACK, "57a7caa44cedff925d23cd5d3f62bc06e83d3485551dcc4f9db2da7d9f8a9694");

    public static final Block COAL_TATER = registerTaterBlock("coal_tater", Blocks.COAL_BLOCK, "7eb25d3f8fcf48673ad0b171ea37154b43d57f6ab04d8ffb546fc606b8505bf4");
    public static final Block DIAMOND_TATER = registerTaterBlock("diamond_tater", Blocks.DIAMOND_BLOCK, "a399c9d599e0d9dc6a480e85f4dbecc45b318814026895ac8150fd2e2fa2599e");
    public static final Block EMERALD_TATER = registerTaterBlock("emerald_tater", ParticleTypes.HAPPY_VILLAGER, "cd76730df726b8ee9d72a3a478457d313626133de1d76c26cfc6af8e80e9c476");
    public static final Block GOLD_TATER = registerTaterBlock("gold_tater", Blocks.GOLD_BLOCK, "180a7cc71153b89a536c148d2f1012d6772a7d3ba8321f922a6de46773c35af9");
    public static final Block IRON_TATER = registerTaterBlock("iron_tater", Blocks.IRON_BLOCK, "174858c976f0274ebce3f3ffcef653609f29d37e0cc9cad25e586864b806cb23");
    public static final Block LAPIS_TATER = registerTaterBlock("lapis_tater", Blocks.LAPIS_BLOCK, "58d5cbda5c5046bf0b0f0d447c2fcc5e468707b6a4837c083af8e109aba9ce1c");
    public static final Block NETHERITE_TATER = registerTaterBlock("netherite_tater", Blocks.NETHERITE_BLOCK, "664dce4fade8e5f352001eff6900d9d4b142935ebed303106539f7ad0193621f");
    public static final Block QUARTZ_TATER = registerTaterBlock("quartz_tater", Blocks.QUARTZ_BLOCK, "7e7b4561d09d1a726fec3607706c9e3c77e8fc9b8c7e9c3637ca80ea0c86be21");
    public static final Block REDSTONE_TATER = registerRedstoneTaterBlock("redstone_tater", new DustParticleEffect(DustParticleEffect.RED, 1), "c47dd2536f5a5eb2bdb1ea4389d3af8ca2fd9d5d2c97c660fc5bf4d970c974de");

    public static final Block COPPER_TATER = registerTaterBlock("copper_tater", ParticleTypes.SCRAPE, "18207c7cf4007222691750b0783d6959261ddf72980483f7c9fcf96c2cba85b1");
    public static final Block EXPOSED_COPPER_TATER = registerTaterBlock("exposed_copper_tater", ParticleTypes.SCRAPE, "bd5020090643edb5ec25d87cb1f408aad4f6018ec4bbe83d25a031ef1e705e4d");
    public static final Block WEATHERED_COPPER_TATER = registerTaterBlock("weathered_copper_tater", ParticleTypes.SCRAPE, "7d9c61d68241667f8e462d18a84c1413ce3db13f93223912201c21327b4adb25");
    public static final Block OXIDIZED_COPPER_TATER = registerTaterBlock("oxidized_copper_tater", ParticleTypes.SCRAPE, "c7e9172d0ec2d20588bf5596ea79403e3da26975cdc5b6d0624e31857774298c");

    public static final Block CAKE_TATER = registerTaterBlock("cake_tater", Blocks.CAKE, "6d46fd58fd566bc0a90f8bc921daf0d9920591a5b153e64a80bb6d54dfb415b9");
    public static final Block ENDERTATER = registerTaterBlock("endertater", ParticleTypes.PORTAL, "1f4f68547ec0d04a1a5c52ce8bb84847cd07674d50cf0740bf6f9c505826892d");
    public static final Block FURNACE_TATER = registerTaterBlock("furnace_tater", ParticleTypes.SMALL_FLAME, "80bdb710ee3d17de73bcf51dfcbc1f61e4dc3fd7a751d517bfd814b202b907bb");
    public static final Block MELON_TATER = registerTaterBlock("melon_tater", Items.MELON_SLICE, "a3d21fbdca84efe016f75075ce55cf11382bf03bb691336b1e0f84c727d8c271");
    public static final Block PUMPKIN_TATER = registerTaterBlock("pumpkin_tater", Blocks.PUMPKIN, "21004377d30b55fd2f176e50e431ba88bd9eb8f353b103a67098a3fcbc12119d");
    public static final Block JACK_O_TATER = registerTaterBlock("jack_o_tater", Blocks.JACK_O_LANTERN, "16772b77233f9d9035436287861b206ac13112d552a6c8e9754b26486b1e5bd");
    public static final Block SCULK_TATER = registerTaterBlock("sculk_tater", ParticleTypes.SOUL, "4265450388096aeb3d228c3b99f6ec64ea4a1a846c9903c7d9db1c309e27469b");
    public static final Block SLIME_TATER = registerTaterBlock("slime_tater", Blocks.SLIME_BLOCK, "16747a7e1605794debfbb43befda2ce986075b3969e0b247ddd7bc6cdaa56a51");
    public static final Block HEROBRINE_TATER = registerElderGuardianParticleTaterBlock("herobrine_tater", "6e9ca544a4561b8cbcdb35820779960497ea748d685e2b17c814a18dc19147fd");
    public static final Block OCHRE_FROGLIGHT_TATER = registerTaterBlock("ochre_froglight_tater", ParticleTypes.SPIT, "92c34818ed06f3d76af6daabe87ab5b9a8c425865eaed0f873470af458b3152e");
    public static final Block PEARLESCENT_FROGLIGHT_TATER = registerTaterBlock("pearlescent_froglight_tater", ParticleTypes.SPIT, "b7e33a69c99e044b98d0a11b830ef7b764d6de9874f24b4f44610f565b76fab1");
    public static final Block VERDANT_FROGLIGHT_TATER = registerTaterBlock("verdant_froglight_tater", ParticleTypes.SPIT, "96ae96a834b8eee8edadaaef368f4b0c93a34b2a634ef89efc14326d965f0040");
    public static final Block SNOWMAN_TATER = registerTaterBlock("snowman_tater", ParticleTypes.SNOWFLAKE, "89ceb42efcfc372cbb26f817e1707a16864af16e0e37c793db05b16cd1f82ac");

    public static final Block ACACIA_TATER = registerTaterBlock("acacia_tater", Blocks.ACACIA_PLANKS, "807d75cb114e057e5156af324517e87012d11566beb112152be4a94a65273535");
    public static final Block ANDESITE_TATER = registerTaterBlock("andesite_tater", Blocks.ANDESITE, "5b728494483d695a171affd93730b65271726a5c8840d96a1acbf64b4dbdb555");
    public static final Block BAMBOO_TATER = registerTaterBlock("bamboo_tater", Blocks.BAMBOO, "acecf9447693e769f17469d67251cddb4b93e5b64efe7445d04ea286a50d8a3c");
    public static final Block BARRIER_TATER = registerMarkerTaterBlock("barrier_tater", Blocks.BARRIER, "d7f155b1a7627408e8f8c670fbd302c8fc0e320e5320b4fc517abfcfe02ab046");
    public static final Block BEDROCK_TATER = registerTaterBlock("bedrock_tater", Blocks.BEDROCK, "c782d793af7aa0ebf5f5c9fdde3f636f93683368bf1daa544a449342b48355b");
    public static final Block BIRCH_TATER = registerTaterBlock("birch_tater", Blocks.BIRCH_PLANKS, "c66003fcb840ebcd18f300b9facd1b8b936ef82e1ea6c7ca26b2c5b2bda5f007");
    public static final Block BONE_TATER = registerTaterBlock("bone_tater", Blocks.BONE_BLOCK, "72b59c778f6656bb8502810eac82997d3669563f4fdd6d24d5ac008d334e3172");
    public static final Block BRAIN_CORAL_TATER = registerTaterBlock("brain_coral_tater", ParticleTypes.BUBBLE, "edf7e38eb4130e94bc59412d480b91529206ada944ed4c698a5ef957917f32f8");
    public static final Block BRICK_TATER = registerTaterBlock("brick_tater", Blocks.BRICKS, "98adbfaa40e423ca12560484d26e3a049739a810490fa0b1564a765b1b65137d");
    public static final Block BUBBLE_CORAL_TATER = registerTaterBlock("bubble_coral_tater", ParticleTypes.BUBBLE, "7dc1f18b5b4ccce2171796002bc434e49c53aeb23829e4d6f1d505d340522248");
    public static final Block CACTUS_TATER = registerTaterBlock("cactus_tater", ParticleTypes.DAMAGE_INDICATOR, "69ebd5e707f1da0df71ecded8c5a6d853f3c47943bd5f167d029de7ad01f846d");
    public static final Block CHORUS_TATER = registerTaterBlock("chorus_tater", ParticleTypes.REVERSE_PORTAL, "dc85f3160fecd3659eb4fabb9544c130688ec9219f2031592c1abc5f63b80422");
    public static final Block CLAY_TATER = registerTaterBlock("clay_tater", Blocks.CLAY, "f9e2f8d3075e9bb78e237291c0a7f2cc17aff9959c735fba1863d243ae5fe3fc");
    public static final Block CRIMSON_TATER = registerTaterBlock("crimson_tater", Blocks.CRIMSON_PLANKS, "742731b12a9ffa0b0550b3e41c1f3d347ffb65d840dee2c8d22ec7bd2284050a");
    public static final Block DARK_OAK_TATER = registerTaterBlock("dark_oak_tater", Blocks.DARK_OAK_PLANKS, "f9df377e9f8647787a254792ae1fb1a6d03a7515642c26b74562572ffd51517d");
    public static final Block DARK_PRISMARINE_TATER = registerTaterBlock("dark_prismarine_tater", ParticleTypes.BUBBLE_COLUMN_UP, "98ec7227474cff245b820d30e59df47d1bf3b895dc8287e9de08102f6836a407");
    public static final Block DIORITE_TATER = registerTaterBlock("diorite_tater", Blocks.DIORITE, "162b67a6a24a8f0ed29dd33ba143038cebd346f8c87e0c38e532aa9957134be1");
    public static final Block DIRT_TATER = registerTaterBlock("dirt_tater", Blocks.DIRT, "15e74c322a4715d9a4c13595eb6cc720a4afef29c4da0892ebf4fc6ddd4cd1ae");
    public static final Block END_STONE_TATER = registerTaterBlock("end_stone_tater", ParticleTypes.END_ROD, "87b4943c4f8f658439899673e91ac15815ab28b5c9eb72c2bd371e08fd1ac0b");
    public static final Block FIRE_CORAL_TATER = registerTaterBlock("fire_coral_tater", ParticleTypes.BUBBLE, "beadd606c1c7095851ffa38aa8dff23f22dcd78c4449c0e8ca50089252d907c2");
    public static final Block GRANITE_TATER = registerTaterBlock("granite_tater", Blocks.GRANITE, "55cdae99f39b299f65d0a6ec3bd1fde2992689a664668482ab1d8a6d79dfc6e4");
    public static final Block GRASS_TATER = registerTaterBlock("grass_tater", Blocks.GRASS_BLOCK, "a999a144cbe51321291ed00b073511a102d1dbbd7e8bff53fa33a2738f105a75");
    public static final Block HAY_TATER = registerTaterBlock("hay_tater", ParticleTypes.HEART, "df65853f3ebf7c03e477fea14b19345b284bf591476461ac36d399c0dbb144fb");
    public static final Block HONEY_TATER = registerTaterBlock("honey_tater", ParticleTypes.DRIPPING_HONEY, "623f2ad832c27e145076f1267875547444dfb0cef80403921ed4d03a0859fae5");
    public static final Block HONEYCOMB_TATER = registerTaterBlock("honeycomb_tater", ParticleTypes.LANDING_HONEY, "e917928e35f2fac59aa1d14d80c478f38d14e2f66ee59d4eb9d025605406481");
    public static final Block HORN_CORAL_TATER = registerTaterBlock("horn_coral_tater", ParticleTypes.BUBBLE, "3e17f4d2977dafce018ac06163f2a7c5672855d198da86fa050f27a7c45b2da4");
    public static final Block JUNGLE_TATER = registerTaterBlock("jungle_tater", Blocks.JUNGLE_PLANKS, "b9105eebf99ec35ac08aaa2ed8ac721590adbdb1eb3a158a82c11e71e76d28aa");
    public static final Block LIGHT_TATER = registerLightTaterBlock("light_tater", "640ed4ea72aed9503c0519d3380ac480a20f9155c428d0571ad767eb4e8973b4");
    public static final Block MYCELIUM_TATER = registerTaterBlock("mycelium_tater", ParticleTypes.MYCELIUM, "f3447bbb99321b399b7a1913d9bc4e90e8b0fc9b9520af45a8f59a1540d4b620");
    public static final Block NETHER_WART_TATER = registerTaterBlock("nether_wart_tater", ParticleTypes.CRIMSON_SPORE, "81c05e8c91a4ca83120799053270dfd7fdf1376988a1a393645140ae89eb6762");
    public static final Block OAK_TATER = registerTaterBlock("oak_tater", Blocks.OAK_PLANKS, "28cfc208966e2f5206b1aba0489e5fdb3f05e6d94befbc618ad80e74eb1016d2");
    public static final Block OBSIDIAN_TATER = registerTaterBlock("obsidian_tater", Blocks.OBSIDIAN, "38adea8f484dba5baf67ace3ee7675758e2318868b2d72542ed3fff0d009f21");
    public static final Block PODZOL_TATER = registerTaterBlock("podzol_tater", Blocks.PODZOL, "5fe3cfce7c241fdaefe8d0255a545d2c329c1283232d61eaa7324b30a8f478f2");
    public static final Block PRISMARINE_BRICK_TATER = registerTaterBlock("prismarine_brick_tater", ParticleTypes.BUBBLE_COLUMN_UP, "6fdb11b2147b36ed496639bcf8bea8d07e45de610ec08223ca3d56aefb8d81ce");
    public static final Block PRISMARINE_TATER = registerTaterBlock("prismarine_tater", ParticleTypes.NAUTILUS, "abae0abdc506014751e89fa65bb802c81fc2ee3ed0e0a06582abb5bdd088e440");
    public static final Block RED_SAND_TATER = registerTaterBlock("red_sand_tater", new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.RED_SAND.getDefaultState()), "7789a0f57334248b602322f57fb2ed708484bb6f7774442c7f2f2bf2ec09064f");
    public static final Block SAND_TATER = registerTaterBlock("sand_tater", new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.SAND.getDefaultState()), "6d5332076f20d3f1ad766f4bd83649c573d1b3bf67abfa5be26cdf5ca4a948c8");
    public static final Block SEA_LANTERN_TATER = registerTaterBlock("sea_lantern_tater", ParticleTypes.NAUTILUS, "ccf764156bb262a3d47f4b885716aa7749077c56a31aa41248936255ed38e27");
    public static final Block SHROOMLIGHT_TATER = registerTaterBlock("shroomlight_tater", Blocks.SHROOMLIGHT, "c7aae4d5e03d8a758cdce91bd74573abc5306dfca6e44e5d0e14ca6c14085b36");
    public static final Block SHULKER_TATER = registerTaterBlock("shulker_tater", ParticleTypes.END_ROD, "e5efd875ee5f4a37ad05d392a65886c6dcaf10188c9199673b782b795a29a231");
    public static final Block SMOOTH_STONE_TATER = registerTaterBlock("smooth_stone_tater", Blocks.SMOOTH_STONE, "8f7c81245a444328cff8a366d5a55a6897df99f89185adb76b9225551bfc6786");
    public static final Block SOUL_SAND_TATER = registerTaterBlock("soul_sand_tater", ParticleTypes.SOUL, "5750c407d6205c6d572eddbcc162e2e8dd61f3a26fb0eaa7dff99bcf8d03519a");
    public static final Block SPONGE_TATER = registerTaterBlock("sponge_tater", ParticleTypes.DRIPPING_WATER, "697a4102627b55490614f475d4fde0df51a8d0c45e9573799d198f4deca33a14");
    public static final Block SPRUCE_TATER = registerTaterBlock("spruce_tater", Blocks.SPRUCE_PLANKS, "fe400308900536545f886084d9f465f17311a41b7410cfaba323a5ac3b1b9a9c");
    public static final Block STONE_BRICK_TATER = registerTaterBlock("stone_brick_tater", Blocks.STONE_BRICKS, "bdf3cdb5266539c34485d439c6a07baa3fbe5556d980ef14483cd6f5271b089b");
    public static final Block STRUCTURE_VOID_TATER = registerMarkerTaterBlock("structure_void_tater", Blocks.STRUCTURE_VOID, "94903af59c0a5529ac3c9135683f7671090a403c641e5cbde8b21abe4a1e114c");
    public static final Block TARGET_TATER = registerTargetTaterBlock("target_tater", "386b52ed3d2b2a682b4c6f1d40a9ceabf72e7c5b60b8d92d2dcee97a8799450f");
    public static final Block TERRACOTTA_TATER = registerTaterBlock("terracotta_tater", Blocks.TERRACOTTA, "57f120d92f3f076352b682345fdb82204b920e59d226c4c6d2c64d5abc6860e1");
    public static final Block TNTATER = registerTaterBlock("tntater", ParticleTypes.EXPLOSION, "440d175ded62ff7b3cf9de979196e7b95da8a25e9e888c4bed06f5c011dc54a8", 10);
    public static final Block TUBE_CORAL_TATER = registerTaterBlock("tube_coral_tater", ParticleTypes.BUBBLE, "dad0a4ef4c9994ee32a02fcb7960600a295b31a6fef94652998189295fd2ee84");
    public static final Block WARPED_TATER = registerTaterBlock("warped_tater", Blocks.WARPED_PLANKS, "eb4db1936577cfcbbace1dbed72483482af32d99ecd090c8f145cac88ae4e8a2");
    public static final Block WARPED_WART_TATER = registerTaterBlock("warped_wart_tater", ParticleTypes.WARPED_SPORE, "9748cf1c78cfdea730a7ce05ae973a8afe326dc2f71f82a45232d9c6b08776d9");
    public static final Block WOOL_TATER = registerTaterBlock("wool_tater", Blocks.WHITE_WOOL, "ed564fa98b7e8e3abf41779bfe759ca0a3191c8aa70f2eef0af139ba1102e27e");

    public static final Block ACACIA_LOG_TATER = registerTaterBlock("acacia_log_tater", Blocks.ACACIA_LOG, "3b185be7121801b5a956ce462583c55928b31cbea74e4aae82e7330317a1ae60");
    public static final Block ANGRY_BEE_TATER = registerTaterBlock("angry_bee_tater", ParticleTypes.ANGRY_VILLAGER, "21004dc20ce74adbcc31f1588f268b4cb431b501679e1db7451869bd04779b4b");
    public static final Block BEACON_TATER = registerEntityEffectTaterBlock("beacon_tater", "6f7fd952ec5da74a25208853161a15d2f6d022835afe881bd271c710ba25935e");
    public static final Block BEE_NEST_TATER = registerTaterBlock("bee_nest_tater", ParticleTypes.DRIPPING_HONEY, "37b0b38538fac97d26241bf51212a26667596b1b14f1307432c3dcef033af1d0");
    public static final Block BEE_TATER = registerTaterBlock("bee_tater", ParticleTypes.FALLING_HONEY, "80480b902bb32e2b145bb5262629ad7a920d3600365d3101936efc35aad830bd");
    public static final Block BEEHIVE_TATER = registerTaterBlock("beehive_tater", ParticleTypes.DRIPPING_HONEY, "f508a30a1bbc65fbfc58da5dc15d4e930b23b1b48afe72923c506a27cbe06366");
    public static final Block BIRCH_LOG_TATER = registerTaterBlock("birch_log_tater", Blocks.BIRCH_LOG, "6950c69d92a635cb945f845a1a5c428a8e0a0cc2a8b0c563538ec171c4dcee6");
    public static final Block BLACKSTONE_TATER = registerTaterBlock("blackstone_tater", Blocks.BLACKSTONE, "e67acd12c3dee918cc5f3b1a88ab7367549caf978357d79d19d2d486b7acc298");
    public static final Block BLAZE_TATER = registerTaterBlock("blaze_tater", ParticleTypes.FLAME, "85e678d6edab035d25841cfb4c90b631a7242e9d4cf6bcf00f168b8bf7cd290d");
    public static final Block BOOKSHELF_TATER = registerTaterBlock("bookshelf_tater", ParticleTypes.ENCHANT, "dbe032fa6759ba137e52abda44c099344698d80b64257c9295213af168352cae");
    public static final Block BROWN_MOOSHROOM_TATER = registerTaterBlock("brown_mooshroom_tater", ParticleTypes.HEART, "12b92975d95348d7cb94133afcb11b56fc0b4b1c373647dda83876442eee2d41");
    public static final Block BROWN_MUSHROOM_TATER = registerTaterBlock("brown_mushroom_tater", Blocks.BROWN_MUSHROOM_BLOCK, "d5268b990a497d6608f82ba664a8c7c981c28055aac34895f230cbdb284b67b8");
    public static final Block CAVE_SPIDER_TATER = registerTaterBlock("cave_spider_tater", ParticleTypes.SPORE_BLOSSOM_AIR, "9499105f458b25a1e85b7312d00b4f1e1d71d5fd08e2165d77693ac2cab2f2a8");
    public static final Block COBBLED_DEEPSLATE_TATER = registerTaterBlock("cobbled_deepslate_tater", Blocks.COBBLED_DEEPSLATE, "2b378b15b46b469b36da1fb0ac41f2d61df66dd315d5b00714cf7564d6a1eea2");
    public static final Block COBBLESTONE_TATER = registerTaterBlock("cobblestone_tater", Blocks.COBBLESTONE, "7effa3c069da9199503f2fecc1db83fe843491200ba5776d6e799cd24a771c44");
    public static final Block COCOA_TATER = registerTaterBlock("cocoa_tater", Blocks.COCOA, "c4f61e517ea2118d00fc68d3d88abd50b59f3c6f36a63b51793c8699c0a52440");
    public static final Block COLD_STRIDER_TATER = registerTaterBlock("cold_strider_tater", ParticleTypes.SNOWFLAKE, "a71ba7597744db25f0efa20b87c3e6aa284385dfacf24952c80b87afa7f6cc84");
    public static final Block COW_TATER = registerTaterBlock("cow_tater", ParticleTypes.HEART, "945f14291e91f120e9afba2729302cb75e7eab2afd7f3c39129f14dfc8061c55");
    public static final Block CRAFTING_TATER = registerTaterBlock("crafting_tater", Blocks.CRAFTING_TABLE, "c888ec7f32ee555a29e073600961f90618203e60baf13a077f4821c4b98bd62f");
    public static final Block CRIMSON_NYLIUM_TATER = registerTaterBlock("crimson_nylium_tater", ParticleTypes.CRIMSON_SPORE, "c47f7ef4d21df90eaa2cf876d53a02773b13262f90324b03d94546fa0b52cb5a");
    public static final Block CRIMSON_STEM_TATER = registerTaterBlock("crimson_stem_tater", Blocks.CRIMSON_STEM, "247fee4d47e209761521a3adc8f79c3f69b9cd6083849a279d375e847fca369d");
    public static final Block DARK_OAK_LOG_TATER = registerTaterBlock("dark_oak_log_tater", Blocks.DARK_OAK_LOG, "623ceb12251eb2e570210a8433213078d3b512b759e2906f6311d2fcc9524886");
    public static final Block DAYLIGHT_DETECTOR_TATER = registerDaylightDetectorTaterBlock("daylight_detector_tater", "3523f4c9e9bccbd42ababb9f8fce50f1a27260efe00d20a425c6b2968a7af227", false);
    public static final Block DEAD_BRAIN_CORAL_TATER = registerTaterBlock("dead_brain_coral_tater", ParticleTypes.BUBBLE_POP, "5db8256bd78f3e34e2666ae29f29433aa7187525fbf3e539c3bf010a28ca3935");
    public static final Block DEAD_BUBBLE_CORAL_TATER = registerTaterBlock("dead_bubble_coral_tater", ParticleTypes.BUBBLE_POP, "bc0d41055df5ba2342dedd76fedb0d7f3f2d992ebdb5870e426c61dcf2c67686");
    public static final Block DEAD_FIRE_CORAL_TATER = registerTaterBlock("dead_fire_coral_tater", ParticleTypes.BUBBLE_POP, "88cb70f602f9bc3064c34f80f47148a7a6c32ddc6f5580dd09fae47812e95d31");
    public static final Block DEAD_HORN_CORAL_TATER = registerTaterBlock("dead_horn_coral_tater", ParticleTypes.BUBBLE_POP, "a5ae31b1dbdd251643835268750699b880d83c2cee7578745ccc6adb2780d192");
    public static final Block DEAD_TUBE_CORAL_TATER = registerTaterBlock("dead_tube_coral_tater", ParticleTypes.BUBBLE_POP, "236d9b535fe8fb6f0bca188f5a6feda3c72423d2bf3d226a85983d5805319b72");
    public static final Block DEEPSLATE_BRICK_TATER = registerTaterBlock("deepslate_brick_tater", Blocks.DEEPSLATE_BRICKS, "3853ed89b5e05717e7bd00e9a33d7e65ebde55e81c0ef868fecc7d2158bc6a1f");
    public static final Block DEEPSLATE_TATER = registerTaterBlock("deepslate_tater", Blocks.DEEPSLATE, "b76d7b1d85b8b724930470eb2986b914b85c4983fc5651a55f09571ababc8b42");
    public static final Block DRIED_KELP_TATER = registerTaterBlock("dried_kelp_tater", Blocks.DRIED_KELP_BLOCK, "bc2abdca594bfb97b2a4b49d88ef7bc01969171a89be411426ce5c0f94fa7a94");
    public static final Block DROWNED_TATER = registerTaterBlock("drowned_tater", ParticleTypes.NAUTILUS, "5ea6f03c0cab4a968976ad95beaba4c60ab46755aa7ff8400b2d87383df6b885");
    public static final Block EYE_OF_ENDER_TATER = registerTaterBlock("eye_of_ender_tater", ParticleTypes.REVERSE_PORTAL, "36fc9fc2b0ab94a11303e3efb8b2534662e5d11ac8a9b9310b588a512eaab55e");
    public static final Block FOX_TATER = registerTaterBlock("fox_tater", ParticleTypes.HEART, "d0d40bc2aa788d6f9d0e3fcde50714f3f47d5db64f3d6a11b2c3fad2b65ba1ea");
    public static final Block GHAST_TATER = registerTaterBlock("ghast_tater", ParticleTypes.DRIPPING_OBSIDIAN_TEAR, "66585c4fc057e15bfc28db7ae8ac778016504f31d5422dfda2345967ba53c44f");
    public static final Block GILDED_BLACKSTONE_TATER = registerTaterBlock("gilded_blackstone_tater", Blocks.GILDED_BLACKSTONE, "987e60212d2d60123e26d1be977de784aa1afc781522645c9a74e492f2295e67");
    public static final Block GLOW_SQUID_TATER = registerTaterBlock("glow_squid_tater", ParticleTypes.GLOW_SQUID_INK, "edd69cada8e6095770ffcfc91fd6df851630efd3bad08dfce78bcb30a5702909");
    public static final Block GLOWSTONE_TATER = registerTaterBlock("glowstone_tater", ParticleTypes.GLOW, "58283dd855f99826c8e938d0f162f111b067bad26685e606b35c847946e4f38f");
    public static final Block HUSK_TATER = registerTaterBlock("husk_tater", Items.ROTTEN_FLESH, "26a88dfb103e0a938776bb27d464bdbc02d39229869975179f1376808a6744b5");
    public static final Block INVERTED_DAYLIGHT_DETECTOR_TATER = registerDaylightDetectorTaterBlock("inverted_daylight_detector_tater", "b721a9503c2c2b25e5dffe5bad1f8bd9f4f620f5ad79a4568322b405973426dd", true);
    public static final Block JUNGLE_LOG_TATER = registerTaterBlock("jungle_log_tater", Blocks.JUNGLE_LOG, "fd9658650626b1c63fe8fa5f10e5a981a400532de45153cfe481f302d8980818");
    public static final Block MAGMA_CUBE_TATER = registerTaterBlock("magma_cube_tater", ParticleTypes.DRIPPING_LAVA, "b34a0d09a2721e18afd3f4c1f2abf630734566e5e04054cc086945dd3af4a1b3");
    public static final Block MOOBLOOM_TATER = registerTaterBlock("moobloom_tater", ParticleTypes.COMPOSTER, "a5a022dae96e419275a0bf5dfabde2d1c4dd073376d52265bee5dd67776ee5a2");
    public static final Block MOOLIP_TATER = registerTaterBlock("moolip_tater", ParticleTypes.CHERRY_LEAVES, "271e15bc807ccf8ecdd594bac37680c06aeb7ec46ce071f225cf207d718654a2");
    public static final Block MUDDY_PIG_TATER = registerTaterBlock("muddy_pig_tater", ParticleTypes.HEART, "70137b92b07e1c24c6d3042daa2997a871383c7278d88e3ecf1964e4b75ff993");
    public static final Block MUSHROOM_STEM_TATER = registerTaterBlock("mushroom_stem_tater", Blocks.MUSHROOM_STEM, "104dd3bad53758af4eb92be90e78277a6201e63d63614c463c5156d210a61c11");
    public static final Block NETHER_BRICK_TATER = registerTaterBlock("nether_brick_tater", Blocks.NETHER_BRICKS, "751534292b1f5e13c8cea2d4f4f653c9f46cb63f5e2ad480c24fc98f22a027c0");
    public static final Block NETHERRACK_TATER = registerTaterBlock("netherrack_tater", Blocks.NETHERRACK, "787fea3e6c6da9693d74b0607d6c7c8081dd75458a5b52110a4761d30775040f");
    public static final Block OAK_LOG_TATER = registerTaterBlock("oak_log_tater", Blocks.OAK_LOG, "13d4a4903b8374b799549775e0f9d45bbd9b2b219edff4d1ddb268472ca47a29");
    public static final Block PIG_TATER = registerTaterBlock("pig_tater", ParticleTypes.HEART, "530a64b89a9a46c58300eae437817789afab264c0be187b6f2733ab24adc8480");
    public static final Block POLAR_BEAR_TATER = registerTaterBlock("polar_bear_tater", Blocks.SNOW_BLOCK, "92a9da572909635f119bdaa54162991f44c99812fa3e504fe2305e2c8210ddf2");
    public static final Block PUFFERTATER = registerTaterBlock("puffertater", ParticleTypes.DRIPPING_WATER, "4d777aa416424b7bb713c158117f7392a74ebd1fe49eff6fa2c4b1192720eb48");
    public static final Block RED_MOOSHROOM_TATER = registerTaterBlock("red_mooshroom_tater", ParticleTypes.HEART, "3f41d3f6948a08ca1388b31cde2d629364ccb2a21a4b20bfec618ec37637e803");
    public static final Block RED_MUSHROOM_TATER = registerTaterBlock("red_mushroom_tater", Blocks.RED_MUSHROOM_BLOCK, "2730185a1933b6e90c1d4ab439aeac51259f428732b4cb38fcfe4988cb0994d1");
    public static final Block RED_NETHER_BRICK_TATER = registerTaterBlock("red_nether_brick_tater", Blocks.RED_NETHER_BRICKS, "70986bf9dba2c063c3afa5643a79a4adf7379e249bbae67861ae3eb89b8bfe91");
    public static final Block RED_SANDSTONE_TATER = registerTaterBlock("red_sandstone_tater", Blocks.RED_SANDSTONE, "557a2c97c9225ca72cc2507f465a0ff96c949035d8f53a9420d7cdf6f875b805");
    public static final Block RUBY_TATER = registerTaterBlock("ruby_tater", ParticleTypes.HAPPY_VILLAGER, "becb1ee95fba3f868fc36c126e5962244ac4bf64cd35b117871e62590a1660ed");
    public static final Block SANDSTONE_TATER = registerTaterBlock("sandstone_tater", Blocks.SANDSTONE, "dee6a6e0c3521db9b0279d4d5a11f009e196cfe6e1f5babf1e09d99f5c48ba13");
    public static final Block SEA_PICKLE_TATER = registerTaterBlock("sea_pickle_tater", ParticleTypes.UNDERWATER, "ec327a8c1dddd36722345cb12f627e99fdd9b56151f34e2d45794628f08f682a");
    public static final Block SHEEP_TATER = registerTaterBlock("sheep_tater", ParticleTypes.HEART, "2e00da1671dd107fade939288e944bbf02609b13f0d0ff9d4e81448cf37ab26c");
    public static final Block SHIELDED_WITHER_TATER = registerTaterBlock("shielded_wither_tater", ParticleTypes.SOUL, "7331e65f919d20063df086e2423340fac6d594207b525600d0776aec86fadb30");
    public static final Block SNOW_FOX_TATER = registerTaterBlock("snow_fox_tater", ParticleTypes.HEART, "bd1d473e3e82d0afca0f7568748f0ff637334632cfb5f6ac228533fadf9bbbf5");
    public static final Block SPIDER_TATER = registerTaterBlock("spider_tater", Items.SPIDER_EYE, "8c711ae226b0e84806e93986ad175e855e07d8b23c2fc369596549f571c739f0");
    public static final Block SPRUCE_LOG_TATER = registerTaterBlock("spruce_log_tater", Blocks.SPRUCE_LOG, "87a820cdad664cf54f98e36f70c92592c584c8a586003eb6ebfcff2f93cda0dc");
    public static final Block SQUID_TATER = registerTaterBlock("squid_tater", ParticleTypes.SQUID_INK, "9896dd194df00aa0f48474dc75c9bb45fa7813ae50e42c4a69f08c2d1b3c4759");
    public static final Block STRAY_TATER = registerTaterBlock("stray_tater", Blocks.BONE_BLOCK, "2d7952ac41e6dafcd3adeaad656728a06599cfa5173085d42ca4fcdf8fb76c44");
    public static final Block STRIDER_TATER = registerTaterBlock("strider_tater", ParticleTypes.LAVA, "730c64d1460190f05169a9f6d1292b298c7ff9673083aed0a22a221153a0f519");
    public static final Block TURTLE_EGG_TATER = registerTaterBlock("turtle_egg_tater", Blocks.TURTLE_EGG, "7e7054403552090e60bb5f4025e06f5bfb513c989a5e87ad88cfe468129ed7f1");
    public static final Block WARPED_NYLIUM_TATER = registerTaterBlock("warped_nylium_tater", ParticleTypes.WARPED_SPORE, "2c80206143d8e76cee31adf8ae64ba4c67d6335abf123f4eb973cf302f6ca2cf");
    public static final Block WARPED_STEM_TATER = registerTaterBlock("warped_stem_tater", Blocks.WARPED_STEM, "2e471eadd1d8195bd5584e1e266ba4c7148c968fb173735c4a6975b590756af");
    public static final Block WITHER_TATER = registerTaterBlock("wither_tater", ParticleTypes.SOUL, "71346598ada1df7f8bc27dde4af895db1655f5d3d1b7d5d4831556feb84ddca8");

    public static final Block AZALEA_TATER = registerTaterBlock("azalea_tater", Blocks.AZALEA_LEAVES, "dc836641ecb40e775f85f4c71b219120e43080e03b8c84a7f60f1ba8127f2931");
    public static final Block BELL_TATER = registerBellTaterBlock("bell_tater", "a84b726a75b81393e206c19ca508570a8a8db64f88b90775eb451214ab0d7561");
    public static final Block COLD_FROG_TATER = registerTaterBlock("cold_frog_tater", ParticleTypes.SPLASH, "f58f867bb5f570c7ca9afa97a698a3ea22b6218a0445958dc8bacdb53976da2e");
    public static final Block CONDUIT_TATER = registerTaterBlock("conduit_tater", ParticleTypes.NAUTILUS, "c59d90ffba4debacc7f8cb400c40e36e15902166cd5544baac5fbbb830a1c58c");
    public static final Block ELDER_GUARDIAN_TATER = registerElderGuardianParticleTaterBlock("elder_guardian_tater", "386f90f38959c5652ce3bd2840bdb18036d3153f6284e12af07f000663c536fe");
    public static final Block END_STONE_BRICK_TATER = registerTaterBlock("end_stone_brick_tater", ParticleTypes.END_ROD, "5c0a9483d2b43c284dc8abd6149342dc63136f3bb208476e17f434059ece4582");
    public static final Block FLOWER_POT_TATER = registerTaterBlock("flower_pot_tater", ParticleTypes.COMPOSTER, "5378b1804af0875cb00fd350e6116bfd2165db209ed8b8e0c48f984669553f45");
    public static final Block GUARDIAN_TATER = registerTaterBlock("guardian_tater", Blocks.PRISMARINE, "c152df6875886f29a0f1f110c2a54820248ceef688fe150797dde4e9d4863c0e");
    public static final Block ILLAGER_TATER = registerTaterBlock("illager_tater", ParticleTypes.ANGRY_VILLAGER, "522c1190fb561f1dfc994b281eb145955e2e439511c83aaf64fed1ab09ec1316");
    public static final Block ILLUSIONER_TATER = registerEntityEffectTaterBlock("illusioner_tater", "6c31b980a02847d5ac948663e95604e754e0b736d4593c7841332833144f2782");
    public static final Block JUKEBOX_TATER = registerTaterBlock("jukebox_tater", ParticleTypes.NOTE, "75f6f61e3a9035a758174979ca664b26e47ca9b273f324f0921b5ad58bdb5835");
    public static final Block LANTERN_TATER = registerTaterBlock("lantern_tater", ParticleTypes.FLASH, "16c275f657bac1363333abf1db0d18bfabf087fbf3df356ec7f41258bc16b76d", 20);
    public static final Block PIGLIN_TATER = registerTaterBlock("piglin_tater", Blocks.GOLD_BLOCK, "4df6290ac1aff8b179420f2a05baa3c721a5cf49f3bd8f9928a0a57c0cf369e");
    public static final Block PINK_WITHER_TATER = registerTaterBlock("pink_wither_tater", ParticleTypes.HEART, "93592323dbce9eec933aa915af71701b77ee478f7e1dd126501a027b1d430bc7");
    public static final Block PISTON_TATER = registerTaterBlock("piston_tater", Blocks.PISTON, "84a5081444bb13cf75bc8040020e91f39e056cf273cd8ebe528e09100a183e53");
    public static final Block PURPUR_TATER = registerTaterBlock("purpur_tater", ParticleTypes.END_ROD, "9c619e55d60579e21126a5c0b88fd8b8baa98d24d1299ed1515fa1452f6ab898");
    public static final Block SOUL_LANTERN_TATER = registerTaterBlock("soul_lantern_tater", ParticleTypes.SOUL_FIRE_FLAME, "a9419cebd15ebc79fe8eba6984637f3c2892e6d68fcc4caa0f3499cfddec25ce");
    public static final Block SOUL_SOIL_TATER = registerTaterBlock("soul_soil_tater", ParticleTypes.SOUL, "c45eb5699f0fafaa6d8e33227e2e626def520545392dffbfa6a071feec239049");
    public static final Block STICKY_PISTON_TATER = registerTaterBlock("sticky_piston_tater", Blocks.SLIME_BLOCK, "404735be9d3e0dea9cec3d4a035fa4b0668b0a7e79cafecb80decba02ace2451");
    public static final Block TEMPERATE_FROG_TATER = registerTaterBlock("temperate_frog_tater", ParticleTypes.SPLASH, "e2dd56ee8f70c6bbb0fb43ed875071b1ed4a4a8130cb4f8dd17869e1395beb4e");
    public static final Block UNDERWATER_TNTATER = registerTaterBlock("underwater_tntater", ParticleTypes.EXPLOSION, "fd90716acd4dd643bef71a84e44bc5cb75ec8ab260986dd679452778c80f8496", 10);
    public static final Block VEX_TATER = registerTaterBlock("vex_tater", ParticleTypes.ENCHANTED_HIT, "9f15dcbe0bde9f613146ba4d51d5448eaf80d260bdb323d2358371e8c3d50829");
    public static final Block VILLAGER_TATER = registerTaterBlock("villager_tater", ParticleTypes.HAPPY_VILLAGER, "7c34f81b74855b9c6a2da0a9000039871a17631976c9f9d0850ae30e31b98397");
    public static final Block VINDITATER = registerTaterBlock("vinditater", ParticleTypes.ANGRY_VILLAGER, "5ca5f1069c3399ea3eda014b9dc5aac3827116ed93617bb44ba87717b11ff98a");
    public static final Block WANDERING_TRADER_TATER = registerTaterBlock("wandering_trader_tater", ParticleTypes.HAPPY_VILLAGER, "860592fc5385f74ffe8c3e3e9f1ad16a5a865bcfa33697483ff7a5da3a784392");
    public static final Block WARM_FROG_TATER = registerTaterBlock("warm_frog_tater", ParticleTypes.SPLASH, "7ea9de371ba4a3edfe8260a559c7cc25564d2cdbb7097d0d3575908a8b627f10");
    public static final Block WAX_TATER = registerTaterBlock("wax_tater", ParticleTypes.LANDING_HONEY, "acc39045a9f072a3adce91f7dd75ce2385cf6c8c251adb5d7e98ae999ae81777");
    public static final Block WITCH_TATER = registerEntityEffectTaterBlock("witch_tater", "1a02c42db26bda231513924f916c1a37abe89255ab3cb77d7da95469052917f3");
    public static final Block ZOMBIE_VILLAGER_TATER = registerTaterBlock("zombie_villager_tater", Items.ROTTEN_FLESH, "7e37fe7abf9ee78de2889993e24d3fc7784269f03ae65da2d8ab7a59e0d1516f");
    public static final Block ZOMBIFIED_PIGLIN_TATER = registerTaterBlock("zombified_piglin_tater", Items.ROTTEN_FLESH, "248b2a6973bbeb1a3e48c8e0b695d733d5a4e25272a639d248d89cf4129cc2e9");

    public static final Block BONE_SPIDER_TATER = registerTaterBlock("bone_spider_tater", Blocks.BONE_BLOCK, "dcde0b254cf7d0e8a170c667de621c414e7475e06dafa8ada868224a2fa7a29b");
    public static final Block BOULDERING_ZOMBIE_TATER = registerTaterBlock("bouldering_zombie_tater", Items.ROTTEN_FLESH, "ca552c8f68cd8e46bd493e77a1726d7e03a460fc89ec80d273499521cf9831f0");
    public static final Block CHARGED_CREEPER_TATER = registerTaterBlock("charged_creeper_tater", ParticleTypes.EXPLOSION, "16134034e6d24f5f723171de2428c92106cd261d329d965195a9065ff5378b00", 10);
    public static final Block LOBBER_ZOMBIE_TATER = registerTaterBlock("lobber_zombie_tater", Items.ROTTEN_FLESH, "4178a5a659a245900fbdc468f4d421c34fa0d1c7e0ff1f47be9de580648035fe");
    public static final Block MOSSY_SKELETATER = registerTaterBlock("mossy_skeletater", Blocks.MOSS_BLOCK, "9c07242332a0b5c85f69f626b83a28bea93a41c51f7c5c1c9bcadea6ea7f6895");
    public static final Block STRIPPED_ACACIA_LOG_TATER = registerTaterBlock("stripped_acacia_log_tater", Blocks.STRIPPED_ACACIA_LOG, "1ac99e952e6f5e42066139fa692179f23d33281521d65901079e403a52edd430");
    public static final Block STRIPPED_BIRCH_LOG_TATER = registerTaterBlock("stripped_birch_log_tater", Blocks.STRIPPED_BIRCH_LOG, "b67e34704da63d6ba4f3c91fc9aab11b5efc36e0f823d4d9c84ef021149585c4");
    public static final Block STRIPPED_CRIMSON_STEM_TATER = registerTaterBlock("stripped_crimson_stem_tater", Blocks.STRIPPED_CRIMSON_STEM, "ff702bf5fb8a672fd89696a0a09999db06b22cdc4a175fd5366916d84297da82");
    public static final Block STRIPPED_DARK_OAK_LOG_TATER = registerTaterBlock("stripped_dark_oak_log_tater", Blocks.STRIPPED_DARK_OAK_LOG, "439b09449076c58a911d464256c29dd5d634852287630c05f1c3c82a4368ce62");
    public static final Block STRIPPED_JUNGLE_LOG_TATER = registerTaterBlock("stripped_jungle_log_tater", Blocks.STRIPPED_JUNGLE_LOG, "1e811034ee1631e7f560ff35b6bdb5e0b460b559d799740ac59d0e2d2c0bb9e9");
    public static final Block STRIPPED_OAK_LOG_TATER = registerTaterBlock("stripped_oak_log_tater", Blocks.STRIPPED_OAK_LOG, "7cf7cfc6763ba87781f1de7f76ec5a26436c14a74b1292f7d2409f399dd72773");
    public static final Block STRIPPED_SPRUCE_LOG_TATER = registerTaterBlock("stripped_spruce_log_tater", Blocks.STRIPPED_SPRUCE_LOG, "7a1f047b2d61b14e717f6c42f67af5da1d101d0a37340c6e497f3e3af1d62691");
    public static final Block STRIPPED_WARPED_STEM_TATER = registerTaterBlock("stripped_warped_stem_tater", Blocks.STRIPPED_WARPED_STEM, "54453447dfc628793e1c704509fd24dd3effcaff9ce6aba16efbee3a1001d65f");
    public static final Block TROPICAL_SLIME_TATER = registerTaterBlock("tropical_slime_tater", ParticleTypes.FISHING, "473d7ffeeb9562e1c0fe59ac45dbec370750793cd6acbd01e26c3a02cea77fea");

    public static final Block APPLE_TATER = registerTaterBlock("apple_tater", Items.APPLE, "379e6c96a907fce60e8c0c55b02b554663e50f6dff9eee113e3742f8779c0304");
    public static final Block GOLDEN_APPLE_TATER = registerTaterBlock("golden_apple_tater", Items.GOLDEN_APPLE, "27a47cff605fb936bb826109bb2b5e77dc3210e211f2f53c8470cf8ed2c80476");
    public static final Block ICE_TATER = registerTaterBlock("ice_tater", Blocks.ICE, "bc89a947d4fd385418f3352f2137bae390cbdf96d5fb479b59a97029f34e1461");
    public static final Block KING_TATER = registerTaterBlock("king_tater", ParticleTypes.TOTEM_OF_UNDYING, "b8b7fc416de5d8a0f1b6f6a7234e6789fb46fed358522fe90da8f92ceafeea3b");
    public static final Block RAW_COPPER_TATER = registerTaterBlock("raw_copper_tater", Blocks.RAW_COPPER_BLOCK, "e02d263510b34498c8814b11c8deefb754d647c4444db61527b600a88b566204");
    public static final Block RAW_GOLD_TATER = registerTaterBlock("raw_gold_tater", Blocks.RAW_GOLD_BLOCK, "c9cf35e928c3b94a29a6049414cd411bd223da3dd232b7a6bbb34fbf37e5db53");
    public static final Block RAW_IRON_TATER = registerTaterBlock("raw_iron_tater", Blocks.RAW_IRON_BLOCK, "50d19e2e668e4c6329b1491c43a75e8b484fa5ef834ff3bacdb56fcc2d49c235");

    public static final Block ALLAY_TATER = registerTaterBlock("allay_tater", ParticleTypes.HEART, "36911b99859c6627cba710041a7ce4ab17791e5c9278a85c75c1596dd1c789bb");
    public static final Block MANGROVE_LOG_TATER = registerTaterBlock("mangrove_log_tater", Blocks.SPRUCE_LOG, "2ab0654a238462e8f1746431d05dfd13d95260031afe335b696feaa6bb49d678");
    public static final Block MANGROVE_TATER = registerTaterBlock("mangrove_tater", Blocks.CRIMSON_PLANKS, "42ccd79aabbb47ae9bdb6b00ad9f43e65d098cc27caa3a2cd8d94b283f726464");
    public static final Block MUD_BRICK_TATER = registerTaterBlock("mud_brick_tater", Blocks.TERRACOTTA, "e37555d890f905e72083844ca751a661d84ed599ac649dc382174af2180ba12");
    public static final Block MUD_TATER = registerTaterBlock("mud_tater", Blocks.STONE, "f83561c253f837c077d9e5394f71167220f9966aafe1d59a87caacbd0a3e4814");
    public static final Block PACKED_MUD_TATER = registerTaterBlock("packed_mud_tater", Blocks.DIRT, "67a5911061c9497c1e888aabe97370d4d00f900ca798cb1ac8cf41fe7bb41532");
    public static final Block STRIPPED_MANGROVE_LOG_TATER = registerTaterBlock("stripped_mangrove_log_tater", Blocks.STRIPPED_CRIMSON_STEM, "c03d0e7742fa66506b5739c98d0cf29411463608877fa667ce29c7b85793d18a");

    public static final Block LUCY_AXOLOTL_TATER = registerTaterBlock("lucy_axolotl_tater", ParticleTypes.HEART, "721a824dcfa8d7503f008f59d7e4874a58ba6f6351750720c78a55eb24211fb6");
    public static final Block WILD_AXOLOTL_TATER = registerTaterBlock("wild_axolotl_tater", ParticleTypes.HEART, "861a7607987e0751e10c3d819775b58c45dba2911bd5222ca8111a8401248d56");
    public static final Block GOLD_AXOLOTL_TATER = registerTaterBlock("gold_axolotl_tater", ParticleTypes.HEART, "9e8dff1cf4f1e76c76993ab9316ef7904b229c37b8cbd1876ccb8c9bcbd36502");
    public static final Block CYAN_AXOLOTL_TATER = registerTaterBlock("cyan_axolotl_tater", ParticleTypes.HEART, "9d61be444b360184cc39faecfac849359ba56843e1469a3c4687f004b9a7ae65");
    public static final Block BLUE_AXOLOTL_TATER = registerTaterBlock("blue_axolotl_tater", ParticleTypes.HEART, "88bc29626c5311e82f18a692d7b26bec5b841a0533ba633e5887a172c765bf05");

    public static final Block BRONZE_CAPSULE_TATER = registerCapsuleTaterBlock("bronze_capsule_tater", 0x764D22, 90, "7391822504d79491186d42b5ab6fdd9615bfc1886324be5f5b613dcb03319677");
    public static final Block SILVER_CAPSULE_TATER = registerCapsuleTaterBlock("silver_capsule_tater", 0xBFBFBF, 9, "afdce3ea1399dd0b738faaecf89cc5bdcf179b8dc4f3d7964c8cd45c89257fd1");
    public static final Block GOLD_CAPSULE_TATER = registerCapsuleTaterBlock("gold_capsule_tater", 0xF1A00E, 1, "db5388834578ccb906e97d3e54aeb33edcc12d821f081b7eb04830cbd260ad81");

    public static final Block CORRUPTATER = register("corruptater", createTaterBlockSettings(), settings -> new CorruptaterBlock(settings, 2));

    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_ENTITY = FabricBlockEntityTypeBuilder.create(LaunchPadBlockEntity::new, GOLD_LAUNCH_PAD, IRON_LAUNCH_PAD).build();
    public static final BlockEntityType<ContributorStatueBlockEntity> CONTRIBUTOR_STATUE_ENTITY = FabricBlockEntityTypeBuilder.create(ContributorStatueBlockEntity::new, CONTRIBUTOR_STATUE).build();
    public static final BlockEntityType<InfiniteDispenserBlockEntity> INFINITE_DISPENSER_ENTITY = FabricBlockEntityTypeBuilder.create(InfiniteDispenserBlockEntity::new, INFINITE_DISPENSER).build();
    public static final BlockEntityType<InfiniteDropperBlockEntity> INFINITE_DROPPER_ENTITY = FabricBlockEntityTypeBuilder.create(InfiniteDropperBlockEntity::new, INFINITE_DROPPER).build();
    public static final BlockEntityType<TateroidBlockEntity> TATEROID_ENTITY = FabricBlockEntityTypeBuilder.create(TateroidBlockEntity::new, TATEROID, RED_TATEROID, ORANGE_TATEROID, YELLOW_TATEROID, GREEN_TATEROID, BLUE_TATEROID, PURPLE_TATEROID).build();
    public static final BlockEntityType<DaylightDetectorTaterBlockEntity> DAYLIGHT_DETECTOR_TATER_ENTITY = FabricBlockEntityTypeBuilder.create(DaylightDetectorTaterBlockEntity::new, DAYLIGHT_DETECTOR_TATER, INVERTED_DAYLIGHT_DETECTOR_TATER).build();
    public static final BlockEntityType<BellTaterBlockEntity> BELL_TATER_ENTITY = FabricBlockEntityTypeBuilder.create(BellTaterBlockEntity::new, BELL_TATER).build();

    private static Block registerSimple(String id, Block virtual) {
        return register(id, AbstractBlock.Settings.copy(virtual).strength(100), settings -> new SimplePolymerBlock(settings, virtual));
    }

    private static AbstractBlock.Settings createTaterBlockSettings() {
        return AbstractBlock.Settings.create().mapColor(MapColor.PALE_GREEN).strength(100);
    }

    private static Block registerBotanicTaterBlock(String id, ParticleEffect effect, String textureUp, String textureDown) {
        return register(id, createTaterBlockSettings(), settings -> new BotanicalPotatoBlock(settings, textureUp, textureDown, effect, 2));
    }

    private static Block registerTaterBlock(String id, ParticleEffect effect, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new CubicPotatoBlock(settings, effect, texture));
    }

    private static Block registerTaterBlock(String id, Block particleBlock, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new CubicPotatoBlock(settings, particleBlock, texture));
    }

    private static Block registerTaterBlock(String id, Item particleItem, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new CubicPotatoBlock(settings, particleItem, texture));
    }

    private static Block registerTaterBlock(String id, ParticleEffect effect, String texture, int particleRate) {
        return register(id, createTaterBlockSettings(), settings -> new CubicPotatoBlock(settings, effect, texture, particleRate));
    }
  
    private static Block registerColorPatternTaterBlock(String id, int[] pattern, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new ColorPatternTaterBlock(settings, pattern, texture));
    }

    private static Block registerEntityEffectTaterBlock(String id, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new EntityEffectTaterBlock(settings, texture));
    }

    private static Block registerLuckyTaterBlock(String id, String texture, String cooldownTexture) {
        return register(id, createTaterBlockSettings(), settings -> new LuckyTaterBlock(settings, texture, cooldownTexture));
    }

    private static Block registerWardenTaterBlock(String id, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new WardenTaterBlock(settings, texture));
    }

    private static Block registerGlowingLayerTaterBlock(String id, ParticleEffect effect, String texture, GlowingLayerTaterBlock.Pixel[] glowingPixels) {
        return register(id, createTaterBlockSettings(), settings -> new GlowingLayerTaterBlock(settings, effect, texture, glowingPixels));
    }

    private static Block registerDiceTaterBlock(String id) {
        return register(id, createTaterBlockSettings(), settings -> new DiceTaterBlock(settings));
    }

    private static Block registerTateroidBlock(String id, RegistryEntry<SoundEvent> defaultSound, double particleColor, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new TateroidBlock(settings, defaultSound, particleColor, texture));
    }

    private static Block registerColorTaterBlock(String id, DyeColor color, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new ColorTaterBlock(settings, color, texture));
    }

    private static Block registerRedstoneTaterBlock(String id, ParticleEffect effect, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new RedstoneTaterBlock(settings, effect, texture));
    }

    private static Block registerDaylightDetectorTaterBlock(String id, String texture, boolean inverted) {
        return register(id, createTaterBlockSettings(), settings -> new DaylightDetectorTaterBlock(settings, texture, inverted));
    }

    private static Block registerTargetTaterBlock(String id, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new TargetTaterBlock(settings, texture));
    }

    private static Block registerBellTaterBlock(String id, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new BellTaterBlock(settings, texture));
    }

    private static Block registerElderGuardianParticleTaterBlock(String id, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new ElderGuardianParticleTater(settings, texture));
    }

    private static Block registerCapsuleTaterBlock(String id, int color, int weight, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new CapsuleTaterBlock(settings, color, weight, texture));
    }

    private static Block registerMarkerTaterBlock(String id, Block particleBlock, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new MarkerTaterBlock(settings, particleBlock, texture));
    }

    private static Block registerLightTaterBlock(String id, String texture) {
        return register(id, createTaterBlockSettings(), settings -> new LightTaterBlock(settings, texture));
    }

    public static void register() {
        registerOxidizableBlockPair(NEBlocks.TRANSIENT_COPPER_DOOR, NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR);
        registerOxidizableBlockPair(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR, NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR);
        registerOxidizableBlockPair(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR, NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR);

        OxidizableBlocksRegistry.registerWaxableBlockPair(NEBlocks.TRANSIENT_COPPER_DOOR, NEBlocks.TRANSIENT_WAXED_COPPER_DOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(NEBlocks.TRANSIENT_EXPOSED_COPPER_DOOR, NEBlocks.TRANSIENT_WAXED_EXPOSED_COPPER_DOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(NEBlocks.TRANSIENT_WEATHERED_COPPER_DOOR, NEBlocks.TRANSIENT_WAXED_WEATHERED_COPPER_DOOR);
        OxidizableBlocksRegistry.registerWaxableBlockPair(NEBlocks.TRANSIENT_OXIDIZED_COPPER_DOOR, NEBlocks.TRANSIENT_WAXED_OXIDIZED_COPPER_DOOR);

        registerBlockEntity("launch_pad", LAUNCH_PAD_ENTITY);
        registerBlockEntity("contributor_statue", CONTRIBUTOR_STATUE_ENTITY);
        registerBlockEntity("infinite_dispenser", INFINITE_DISPENSER_ENTITY);
        registerBlockEntity("infinite_dropper", INFINITE_DROPPER_ENTITY);
        registerBlockEntity("tateroid", TATEROID_ENTITY);
        registerBlockEntity("daylight_detector_tater", DAYLIGHT_DETECTOR_TATER_ENTITY);
        registerBlockEntity("bell_tater", BELL_TATER_ENTITY);
    }

    private static <T extends Block> T register(String id, Block.Settings settings, Function<Block.Settings, T> factory) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, NucleoidExtras.identifier(id));
        T block = factory.apply(settings.registryKey(key));

        return Registry.register(Registries.BLOCK, key, block);
    }

    private static void registerOxidizableBlockPair(Block less, Block more) {
        OxidizableBlocksRegistry.registerOxidizableBlockPair(less, more);

        // TransientOxidizableDoorBlock#hasRandomTicks is dependent on the above registration,
        // so the cached BlockState#ticksRandomly field must be recomputed with the new result
        for (BlockState state : less.getStateManager().getStates()) {
            state.initShapeCache();
        }
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, BlockEntityType<T> type) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, NucleoidExtras.identifier(id), type);
        PolymerBlockUtils.registerBlockEntity(type);
        return type;
    }
}
