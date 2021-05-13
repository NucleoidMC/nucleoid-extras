package xyz.nucleoid.extras.lobby;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.lobby.item.BlockItemWarning;

public class NEItems {
    public static final Item END_PORTAL = new BlockItemWarning(NEBlocks.END_PORTAL, new Item.Settings(), Items.BLACK_CARPET);
    public static final Item END_GATEWAY = new BlockItemWarning(NEBlocks.END_GATEWAY, new Item.Settings(), Items.BLACK_WOOL);
    public static final Item LIGHT_BLOCK = new BlockItemWarning(NEBlocks.LIGHT_BLOCK, new Item.Settings(), Items.GLOWSTONE);

    public static final Item GOLD_LAUNCH_PAD = new BlockItemWarning(NEBlocks.GOLD_LAUNCH_PAD, new Item.Settings(), Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Item IRON_LAUNCH_PAD = new BlockItemWarning(NEBlocks.IRON_LAUNCH_PAD, new Item.Settings(), Items.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final Item BLACK_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.BLACK_CONCRETE_POWDER, new Item.Settings(), Items.BLACK_CONCRETE_POWDER);
    public static final Item BLUE_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.BLUE_CONCRETE_POWDER, new Item.Settings(), Items.BLUE_CONCRETE_POWDER);
    public static final Item BROWN_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.BROWN_CONCRETE_POWDER, new Item.Settings(), Items.BROWN_CONCRETE_POWDER);
    public static final Item CYAN_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.CYAN_CONCRETE_POWDER, new Item.Settings(), Items.CYAN_CONCRETE_POWDER);
    public static final Item GREEN_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.GREEN_CONCRETE_POWDER, new Item.Settings(), Items.GREEN_CONCRETE_POWDER);
    public static final Item GRAY_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.GRAY_CONCRETE_POWDER, new Item.Settings(), Items.GRAY_CONCRETE_POWDER);
    public static final Item LIGHT_BLUE_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.LIGHT_BLUE_CONCRETE_POWDER, new Item.Settings(), Items.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Item LIGHT_GRAY_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.LIGHT_GRAY_CONCRETE_POWDER, new Item.Settings(), Items.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Item LIME_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.LIME_CONCRETE_POWDER, new Item.Settings(), Items.LIME_CONCRETE_POWDER);
    public static final Item MAGENTA_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.MAGENTA_CONCRETE_POWDER, new Item.Settings(), Items.MAGENTA_CONCRETE_POWDER);
    public static final Item ORANGE_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.ORANGE_CONCRETE_POWDER, new Item.Settings(), Items.ORANGE_CONCRETE_POWDER);
    public static final Item PINK_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.PINK_CONCRETE_POWDER, new Item.Settings(), Items.PINK_CONCRETE_POWDER);
    public static final Item PURPLE_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.PURPLE_CONCRETE_POWDER, new Item.Settings(), Items.PURPLE_CONCRETE_POWDER);
    public static final Item RED_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.RED_CONCRETE_POWDER, new Item.Settings(), Items.RED_CONCRETE_POWDER);
    public static final Item WHITE_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.WHITE_CONCRETE_POWDER, new Item.Settings(), Items.WHITE_CONCRETE_POWDER);
    public static final Item YELLOW_CONCRETE_POWDER = new BlockItemWarning(NEBlocks.YELLOW_CONCRETE_POWDER, new Item.Settings(), Items.YELLOW_CONCRETE_POWDER);
    
    public static void register() {
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "end_portal"), END_PORTAL);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "end_gateway"), END_GATEWAY);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "light_block"), LIGHT_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "gold_launch_pad"), GOLD_LAUNCH_PAD);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "iron_launch_pad"), IRON_LAUNCH_PAD);

        Registry.register(Registry.ITEM, new Identifier("nucleoid", "black_concrete_powder"), BLACK_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "blue_concrete_powder"), BLUE_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "brown_concrete_powder"), BROWN_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "cyan_concrete_powder"), CYAN_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "green_concrete_powder"), GREEN_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "gray_concrete_powder"), GRAY_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "light_blue_concrete_powder"), LIGHT_BLUE_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "light_gray_concrete_powder"), LIGHT_GRAY_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "lime_concrete_powder"), LIME_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "magenta_concrete_powder"), MAGENTA_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "orange_concrete_powder"), ORANGE_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "pink_concrete_powder"), PINK_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "purple_concrete_powder"), PURPLE_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "red_concrete_powder"), RED_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "white_concrete_powder"), WHITE_CONCRETE_POWDER);
        Registry.register(Registry.ITEM, new Identifier("nucleoid", "yellow_concrete_powder"), YELLOW_CONCRETE_POWDER);
    }
}
