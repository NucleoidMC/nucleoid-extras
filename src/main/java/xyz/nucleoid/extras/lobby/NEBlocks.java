package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.block.BasicVirtualBlock;
import eu.pb4.polymer.block.BlockHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.block.DiceTaterBlock;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlock;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlockEntity;
import xyz.nucleoid.extras.lobby.block.TateroidBlock;
import xyz.nucleoid.extras.lobby.block.TateroidBlockEntity;
import xyz.nucleoid.extras.lobby.block.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.block.VirtualEndGatewayBlock;

public class NEBlocks {
    public static final Block END_PORTAL = createSimple(Blocks.END_PORTAL);
    public static final Block END_GATEWAY = new VirtualEndGatewayBlock(AbstractBlock.Settings.of(Material.PORTAL).strength(100).noCollision());
    public static final Block SAFE_TNT = createSimple(Blocks.TNT);

    public static final Block BLACK_CONCRETE_POWDER = createSimple(Blocks.BLACK_CONCRETE_POWDER);
    public static final Block BLUE_CONCRETE_POWDER = createSimple(Blocks.BLUE_CONCRETE_POWDER);
    public static final Block BROWN_CONCRETE_POWDER = createSimple(Blocks.BROWN_CONCRETE_POWDER);
    public static final Block CYAN_CONCRETE_POWDER = createSimple(Blocks.CYAN_CONCRETE_POWDER);
    public static final Block GREEN_CONCRETE_POWDER = createSimple(Blocks.GREEN_CONCRETE_POWDER);
    public static final Block GRAY_CONCRETE_POWDER = createSimple(Blocks.GRAY_CONCRETE_POWDER);
    public static final Block LIGHT_BLUE_CONCRETE_POWDER = createSimple(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
    public static final Block LIGHT_GRAY_CONCRETE_POWDER = createSimple(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    public static final Block LIME_CONCRETE_POWDER = createSimple(Blocks.LIME_CONCRETE_POWDER);
    public static final Block MAGENTA_CONCRETE_POWDER = createSimple(Blocks.MAGENTA_CONCRETE_POWDER);
    public static final Block ORANGE_CONCRETE_POWDER = createSimple(Blocks.ORANGE_CONCRETE_POWDER);
    public static final Block PINK_CONCRETE_POWDER = createSimple(Blocks.PINK_CONCRETE_POWDER);
    public static final Block PURPLE_CONCRETE_POWDER = createSimple(Blocks.PURPLE_CONCRETE_POWDER);
    public static final Block RED_CONCRETE_POWDER = createSimple(Blocks.RED_CONCRETE_POWDER);
    public static final Block WHITE_CONCRETE_POWDER = createSimple(Blocks.WHITE_CONCRETE_POWDER);
    public static final Block YELLOW_CONCRETE_POWDER = createSimple(Blocks.YELLOW_CONCRETE_POWDER);

    public static final Block GOLD_LAUNCH_PAD = new LaunchPadBlock(AbstractBlock.Settings.copy(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).strength(100).noCollision(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    public static final Block IRON_LAUNCH_PAD = new LaunchPadBlock(AbstractBlock.Settings.copy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE).strength(100).noCollision(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final Block TINY_POTATO = createTaterBlock(ParticleTypes.HEART, "ewogICJ0aW1lc3RhbXAiIDogMTYwNjIyODAxMzY0NCwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTczNTE0YTIzMjQ1ZjE1ZGJhZDVmYjRlNjIyMTYzMDIwODY0Y2NlNGMxNWQ1NmRlM2FkYjkwZmE1YTcxMzdmZCIKICAgIH0KICB9Cn0=");
    public static final Block IRRITATER = createTaterBlock(ParticleTypes.ANGRY_VILLAGER, "ewogICJ0aW1lc3RhbXAiIDogMTYwNjI5MzE0MjMyNywKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRiMmNiZmUxZmQ0ZDMxMjM0NjEwODFhZDQ2MGFjYjZjMDM0NWJlZDNmM2NlOTZkNDc1YjVmNThmN2I5MDMwYiIKICAgIH0KICB9Cn0=");
    public static final Block AZALEA_TATER = createTaterBlock(ParticleTypes.HAPPY_VILLAGER, "ewogICJ0aW1lc3RhbXAiIDogMTYyODI3NzEwNDAyNCwKICAicHJvZmlsZUlkIiA6ICI4YmM3MjdlYThjZjA0YWUzYTI4MDVhY2YzNjRjMmQyNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJub3RpbnZlbnRpdmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI2YzA1ZDNiZTkzNjljNjk5ODQ1MTNmMjgxOTMyNjIyYmNhODA3MDA4ZGVmOTk3MjIyYTZkNGY4Y2I3MWQ4MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block STONE_TATER = createTaterBlock(Blocks.STONE, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzQyMzM5Mjc4NSwKICAicHJvZmlsZUlkIiA6ICIzNDkxZjJiOTdjMDE0MWE2OTM2YjFjMjJhMmEwMGZiNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJKZXNzc3N1aGgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJlNTYwMTNhNWY2Mzk5YjVmYjkxYmI2MjBkYzI0MGJiMDRlYjdkMWQ3MWUwODExODUzMDU4YjZkNWVkNjI5MSIKICAgIH0KICB9Cn0=");
    public static final Block CALCITE_TATER = createTaterBlock(Blocks.CALCITE, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzgyMzAxNzA0NywKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ4ZDIyMTViNTYwYzY3NTVhMTIyNGQzMzA0MTgxNGM1ZWRlOTYzOGNjYjQ4MGE5MmJhZjc5MzkwMDgwZTY4NyIKICAgIH0KICB9Cn0=");
    public static final Block TUFF_TATER = createTaterBlock(Blocks.TUFF, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzg1ODQ0MTg4OSwKICAicHJvZmlsZUlkIiA6ICI5MWYwNGZlOTBmMzY0M2I1OGYyMGUzMzc1Zjg2ZDM5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdG9ybVN0b3JteSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NjEyYTlkZmJlMTkwNjExM2M4ZmY4MGJhNzI0YWYxZGNmOGFkNjhmOTVjZjE1ZjhhMjk5MzFiODJkYjViYWRhIgogICAgfQogIH0KfQ==");
    public static final Block DRIPSTONE_TATER = createTaterBlock(Blocks.DRIPSTONE_BLOCK, "ewogICJ0aW1lc3RhbXAiIDogMTYzODU1MTU1NjQzMCwKICAicHJvZmlsZUlkIiA6ICJjNmE2N2QwMmY4MGM0MjhmODYyNmQ5MjhlOTNjN2FjNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJHaW92YW5uaVdpamF5YSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85MDZlYThkY2UxMTYyZTQ2MzUzZDkzNjlmYjkzMTNlOGVjMmE2M2U2NGVhYTYxN2IwMmZlNWYzOTA3M2QwNmQ4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    public static final Block FLAME_TATER = createTaterBlock(ParticleTypes.FLAME, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzc4NjIwMTYxMSwKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY5YTBlNjFiMWViZjAzYTIzYTZlNTg4NWQxMzNjMDA1NWY3NzU1YzNiNjNlM2ZiMmFjNDc1YWFmNGQ4NzQ2NyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block PUZZLE_CUBE_TATER = createTaterBlock(ParticleTypes.FIREWORK, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzg2MDY5NzI1NSwKICAicHJvZmlsZUlkIiA6ICI0M2NmNWJkNjUyMDM0YzU5ODVjMDIwYWI3NDE0OGQxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJrYW1pbDQ0NSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jNjk1YjI3ODE4YTRhMmFhOWZjZmNmY2ViODE2YTdkY2UwOThiYzYyMDFjYzBiZDQ1YjVjMWZlYzBmYmQwMDE0IgogICAgfQogIH0KfQ=");
    public static final Block CRATE_TATER = createTaterBlock(ParticleTypes.WAX_ON, "ewogICJ0aW1lc3RhbXAiIDogMTYzODkwODYyNjk0NywKICAicHJvZmlsZUlkIiA6ICJiYzRlZGZiNWYzNmM0OGE3YWM5ZjFhMzlkYzIzZjRmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4YWNhNjgwYjIyNDYxMzQwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzViN2QzNGYxNmVmOWEzNjE5NjRjZTc0MDViNmI0NjNmNjBjYzIxNGJkYjYyNWUwODk4NjljNTBmNTMzMmY2MWUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
    public static final Block TATER_OF_UNDYING = createTaterBlock(ParticleTypes.TOTEM_OF_UNDYING, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzg2MDQ4MDI5NCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjUyNmQ5MzE0NzgyNWUyZGI0NDRhYWY0YTk0NjRiNjFhZDRlNGRlZmIwYWRmOTQ0YTIyNzU1NDNlZmM5MTkyYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block CRYING_OBSIDIAN_TATER = createTaterBlock(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, "ewogICJ0aW1lc3RhbXAiIDogMTYzODg4MzgyNzAwNiwKICAicHJvZmlsZUlkIiA6ICI5N2ViMDJkMWY5YmI0NjUwYmNmNzE2MTEzYjUzYjY4ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTQmNhY3R1cyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MGQxNTFhMmRhODNiNGEwZWM3M2JlMDhhNDJjNGJjODk2NGZmMWQzYWUyMTA2Y2MyOWI5Yjc5Y2ZjY2I4YjlmIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");


    public static final Block DICE_TATER = createDiceTaterBlock();
    public static final Block TATEROID = createTateroidBlock(SoundEvents.BLOCK_NOTE_BLOCK_BELL);
    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_ENTITY = FabricBlockEntityTypeBuilder.create(LaunchPadBlockEntity::new, GOLD_LAUNCH_PAD, IRON_LAUNCH_PAD).build(null);
    public static final BlockEntityType<TateroidBlockEntity> TATEROID_ENTITY = FabricBlockEntityTypeBuilder.create(TateroidBlockEntity::new, TATEROID).build(null);

    private static Block createSimple(Block virtual) {
        return new BasicVirtualBlock(AbstractBlock.Settings.copy(virtual).strength(100), virtual);
    }

    private static Block createTaterBlock(ParticleEffect effect, String texture) {
        return new TinyPotatoBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), effect, texture);
    }

    private static Block createTaterBlock(Block particleBlock, String texture) {
        return new TinyPotatoBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), particleBlock, texture);
    }

    private static Block createDiceTaterBlock() {
        return new DiceTaterBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100));
    }

    private static Block createTateroidBlock(SoundEvent defaultSound) {
        return new TateroidBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), defaultSound);
    }

    public static void register() {
        register("end_portal", END_PORTAL);
        register("end_gateway", END_GATEWAY);
        register("safe_tnt", SAFE_TNT);
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

        register("tiny_potato", TINY_POTATO);
        register("irritater", IRRITATER);
        register("azalea_tater", AZALEA_TATER);
        register("stone_tater", STONE_TATER);
        register("calcite_tater", CALCITE_TATER);
        register("puzzle_cube_tater", PUZZLE_CUBE_TATER);
        register("tuff_tater", TUFF_TATER);
        register("dripstone_tater", DRIPSTONE_TATER);
        register("flame_tater", FLAME_TATER);
        register("crate_tater", CRATE_TATER);
        register("tater_of_undying", TATER_OF_UNDYING);
        register("crying_obsidian_tater", CRYING_OBSIDIAN_TATER);
        register("dice_tater", DICE_TATER);
        register("tateroid", TATEROID);

        registerBlockEntity("launch_pad", LAUNCH_PAD_ENTITY);
        registerBlockEntity("tateroid", TATEROID_ENTITY);
    }

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(Registry.BLOCK, NucleoidExtras.identifier(id), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, BlockEntityType<T> type) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, NucleoidExtras.identifier(id), type);
        BlockHelper.registerVirtualBlockEntity(type);
        return type;
    }
}
