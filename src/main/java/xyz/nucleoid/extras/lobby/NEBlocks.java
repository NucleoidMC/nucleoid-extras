package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.PolymerMod;
import eu.pb4.polymer.block.BasicVirtualBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlock;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlockEntity;
import xyz.nucleoid.extras.lobby.block.VirtualEndGatewayBlock;

public class NEBlocks {
    public static final Block END_PORTAL = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.PORTAL).strength(100).noCollision(), Blocks.END_PORTAL);
    public static final Block END_GATEWAY = new VirtualEndGatewayBlock(AbstractBlock.Settings.of(Material.PORTAL).strength(100).noCollision());
    public static final Block LIGHT_BLOCK = new BasicVirtualBlock(AbstractBlock.Settings.of(Material.GLASS).strength(100).luminance((state) -> 14), Blocks.BARRIER);

    public static final Block BLACK_CONCRETE_POWDER = createConcretePowder(Blocks.BLACK_CONCRETE_POWDER);
    public static final Block BLUE_CONCRETE_POWDER = createConcretePowder(Blocks.BLUE_CONCRETE_POWDER);
    public static final Block BROWN_CONCRETE_POWDER = createConcretePowder(Blocks.BROWN_CONCRETE_POWDER);
    public static final Block CYAN_CONCRETE_POWDER = createConcretePowder(Blocks.CYAN_CONCRETE_POWDER);
    public static final Block GREEN_CONCRETE_POWDER = createConcretePowder(Blocks.GREEN_CONCRETE_POWDER);
    public static final Block GRAY_CONCRETE_POWDER = createConcretePowder(Blocks.GRAY_CONCRETE_POWDER);
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = createConcretePowder(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = createConcretePowder(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Block LIME_CONCRETE_POWDER = createConcretePowder(Blocks.LIME_CONCRETE_POWDER);
    public static final Block MAGENTA_CONCRETE_POWDER = createConcretePowder(Blocks.MAGENTA_CONCRETE_POWDER);
    public static final Block ORANGE_CONCRETE_POWDER = createConcretePowder(Blocks.ORANGE_CONCRETE_POWDER);
    public static final Block PINK_CONCRETE_POWDER = createConcretePowder(Blocks.PINK_CONCRETE_POWDER);
    public static final Block PURPLE_CONCRETE_POWDER = createConcretePowder(Blocks.PURPLE_CONCRETE_POWDER);
    public static final Block RED_CONCRETE_POWDER = createConcretePowder(Blocks.RED_CONCRETE_POWDER);
    public static final Block WHITE_CONCRETE_POWDER = createConcretePowder(Blocks.WHITE_CONCRETE_POWDER);
    public static final Block YELLOW_CONCRETE_POWDER = createConcretePowder(Blocks.YELLOW_CONCRETE_POWDER);

    public static final Block GOLD_LAUNCH_PAD = new LaunchPadBlock(AbstractBlock.Settings.of(Material.STONE).strength(100).noCollision(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Block IRON_LAUNCH_PAD = new LaunchPadBlock(AbstractBlock.Settings.of(Material.STONE).strength(100).noCollision(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_ENTITY = BlockEntityType.Builder.create(LaunchPadBlockEntity::new, GOLD_LAUNCH_PAD, IRON_LAUNCH_PAD).build(null);

    private static Block createConcretePowder(Block virtual) {
        return new BasicVirtualBlock(AbstractBlock.Settings.of(Material.STONE).strength(100), virtual);
    }

    public static void register() {
        register("end_portal", END_PORTAL);
        register("end_gateway", END_GATEWAY);
        register("light_block", LIGHT_BLOCK);
        register("gold_launch_pad", GOLD_LAUNCH_PAD);
        register("iron_launch_pad", IRON_LAUNCH_PAD);

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

        registerBlockEntity("launch_pad", LAUNCH_PAD_ENTITY);
    }

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(Registry.BLOCK, NucleoidExtras.identifier(id), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, BlockEntityType<T> type) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, NucleoidExtras.identifier(id), type);
        PolymerMod.registerVirtualBlockEntity(NucleoidExtras.identifier(id));
        return type;
    }
}
