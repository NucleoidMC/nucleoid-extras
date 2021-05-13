package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.PolymerMod;
import eu.pb4.polymer.block.BasicVirtualBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlock;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlockEntity;
import xyz.nucleoid.extras.lobby.block.VirtualEndGatewayBlock;

public class NEBlocks {
    public static final Block END_PORTAL = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.PORTAL).strength(100).noCollision(), Blocks.END_PORTAL);
    public static final Block END_GATEWAY = new VirtualEndGatewayBlock(AbstractBlock.Settings.of(Material.PORTAL).strength(100).noCollision());
    public static final Block LIGHT_BLOCK = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.GLASS).strength(100).luminance((state) -> 14), Blocks.BARRIER);

    public static final Block BLACK_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.BLACK_CONCRETE_POWDER);
    public static final Block BLUE_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.BLUE_CONCRETE_POWDER);
    public static final Block BROWN_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.BROWN_CONCRETE_POWDER);
    public static final Block CYAN_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.CYAN_CONCRETE_POWDER);
    public static final Block GREEN_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.GREEN_CONCRETE_POWDER);
    public static final Block GRAY_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.GRAY_CONCRETE_POWDER);
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Block LIME_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.LIME_CONCRETE_POWDER);
    public static final Block MAGENTA_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.MAGENTA_CONCRETE_POWDER);
    public static final Block ORANGE_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.ORANGE_CONCRETE_POWDER);
    public static final Block PINK_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.PINK_CONCRETE_POWDER);
    public static final Block PURPLE_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.PURPLE_CONCRETE_POWDER);
    public static final Block RED_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.RED_CONCRETE_POWDER);
    public static final Block WHITE_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.WHITE_CONCRETE_POWDER);
    public static final Block YELLOW_CONCRETE_POWDER = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), Blocks.YELLOW_CONCRETE_POWDER);

    public static final Block GOLD_LAUNCH_PAD = new LaunchPadBlock(AbstractBlock.Settings.of(Material.STONE).strength(100).noCollision(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Block IRON_LAUNCH_PAD = new LaunchPadBlock(AbstractBlock.Settings.of(Material.STONE).strength(100).noCollision(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_ENTITY;

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "end_portal"), END_PORTAL);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "end_gateway"), END_GATEWAY);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "light_block"), LIGHT_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "gold_launch_pad"), GOLD_LAUNCH_PAD);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "iron_launch_pad"), IRON_LAUNCH_PAD);

        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "black_concrete_powder"), BLACK_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "blue_concrete_powder"), BLUE_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "brown_concrete_powder"), BROWN_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "cyan_concrete_powder"), CYAN_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "green_concrete_powder"), GREEN_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "gray_concrete_powder"), GRAY_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "light_blue_concrete_powder"), LIGHT_BLUE_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "light_gray_concrete_powder"), LIGHT_GRAY_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "lime_concrete_powder"), LIME_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "magenta_concrete_powder"), MAGENTA_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "orange_concrete_powder"), ORANGE_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "pink_concrete_powder"), PINK_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "purple_concrete_powder"), PURPLE_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "red_concrete_powder"), RED_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "white_concrete_powder"), WHITE_CONCRETE_POWDER);
        Registry.register(Registry.BLOCK, new Identifier("nucleoid", "yellow_concrete_powder"), YELLOW_CONCRETE_POWDER);

        LAUNCH_PAD_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "nucleoid:launch_pad", BlockEntityType.Builder.create(LaunchPadBlockEntity::new, GOLD_LAUNCH_PAD, IRON_LAUNCH_PAD).build(null));
        PolymerMod.registerVirtualBlockEntity(new Identifier("nucleoid", "launch_pad"));
    }
}
