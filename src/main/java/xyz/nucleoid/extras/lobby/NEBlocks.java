package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.polymer.api.block.SimplePolymerBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.block.*;

public class NEBlocks {
    public static final Block NUCLEOID_LOGO = createTaterBlock(ParticleTypes.GLOW_SQUID_INK, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDYzNzcxMTc2OSwKICAicHJvZmlsZUlkIiA6ICIzNmMxODk4ZjlhZGE0NjZlYjk0ZDFmZWFmMjQ0MTkxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5haWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JhYzc0MDBkZmNiOWEzODczNjFhM2FkN2MyOTY5NDNlYjg0MWE5YmRhMTNhZDg5NTU4ZTJkNmVmZWJmMTY3YmMiCiAgICB9CiAgfQp9");

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

    public static final Block INFINITE_DISPENSER = new InfiniteDispenserBlock(AbstractBlock.Settings.copy(Blocks.DISPENSER).strength(100));
    public static final Block INFINITE_DROPPER = new InfiniteDropperBlock(AbstractBlock.Settings.copy(Blocks.DROPPER).strength(100));

    public static final Block TINY_POTATO = createTaterBlock(ParticleTypes.HEART, "ewogICJ0aW1lc3RhbXAiIDogMTYwNjIyODAxMzY0NCwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTczNTE0YTIzMjQ1ZjE1ZGJhZDVmYjRlNjIyMTYzMDIwODY0Y2NlNGMxNWQ1NmRlM2FkYjkwZmE1YTcxMzdmZCIKICAgIH0KICB9Cn0=");
    public static final Block IRRITATER = createTaterBlock(ParticleTypes.ANGRY_VILLAGER, "ewogICJ0aW1lc3RhbXAiIDogMTYwNjI5MzE0MjMyNywKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRiMmNiZmUxZmQ0ZDMxMjM0NjEwODFhZDQ2MGFjYjZjMDM0NWJlZDNmM2NlOTZkNDc1YjVmNThmN2I5MDMwYiIKICAgIH0KICB9Cn0=");
    public static final Block SAD_TATER = createTaterBlock(ParticleTypes.FALLING_WATER, "ewogICJ0aW1lc3RhbXAiIDogMTYxMDU1MjI2MjU3MiwKICAicHJvZmlsZUlkIiA6ICJhYTZhNDA5NjU4YTk0MDIwYmU3OGQwN2JkMzVlNTg5MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJiejE0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc5MTVmNWFiNmEzYWY1ZmQ4ZTA0M2JjOThhNTQ2NmFjZmM1ZDU3YzMwZGM5YTFkMmU0YTMyZjdiZmExZDM1YmYiCiAgICB9CiAgfQp9");
    public static final Block AZALEA_TATER = createTaterBlock(Blocks.AZALEA_LEAVES, "ewogICJ0aW1lc3RhbXAiIDogMTYyODI3NzEwNDAyNCwKICAicHJvZmlsZUlkIiA6ICI4YmM3MjdlYThjZjA0YWUzYTI4MDVhY2YzNjRjMmQyNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJub3RpbnZlbnRpdmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWI2YzA1ZDNiZTkzNjljNjk5ODQ1MTNmMjgxOTMyNjIyYmNhODA3MDA4ZGVmOTk3MjIyYTZkNGY4Y2I3MWQ4MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block STONE_TATER = createTaterBlock(Blocks.STONE, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzQyMzM5Mjc4NSwKICAicHJvZmlsZUlkIiA6ICIzNDkxZjJiOTdjMDE0MWE2OTM2YjFjMjJhMmEwMGZiNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJKZXNzc3N1aGgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJlNTYwMTNhNWY2Mzk5YjVmYjkxYmI2MjBkYzI0MGJiMDRlYjdkMWQ3MWUwODExODUzMDU4YjZkNWVkNjI5MSIKICAgIH0KICB9Cn0=");
    public static final Block CALCITE_TATER = createTaterBlock(Blocks.CALCITE, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzgyMzAxNzA0NywKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ4ZDIyMTViNTYwYzY3NTVhMTIyNGQzMzA0MTgxNGM1ZWRlOTYzOGNjYjQ4MGE5MmJhZjc5MzkwMDgwZTY4NyIKICAgIH0KICB9Cn0=");
    public static final Block TUFF_TATER = createTaterBlock(Blocks.TUFF, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzg1ODQ0MTg4OSwKICAicHJvZmlsZUlkIiA6ICI5MWYwNGZlOTBmMzY0M2I1OGYyMGUzMzc1Zjg2ZDM5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdG9ybVN0b3JteSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NjEyYTlkZmJlMTkwNjExM2M4ZmY4MGJhNzI0YWYxZGNmOGFkNjhmOTVjZjE1ZjhhMjk5MzFiODJkYjViYWRhIgogICAgfQogIH0KfQ==");
    public static final Block DRIPSTONE_TATER = createTaterBlock(Blocks.DRIPSTONE_BLOCK, "ewogICJ0aW1lc3RhbXAiIDogMTYzODU1MTU1NjQzMCwKICAicHJvZmlsZUlkIiA6ICJjNmE2N2QwMmY4MGM0MjhmODYyNmQ5MjhlOTNjN2FjNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJHaW92YW5uaVdpamF5YSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85MDZlYThkY2UxMTYyZTQ2MzUzZDkzNjlmYjkzMTNlOGVjMmE2M2U2NGVhYTYxN2IwMmZlNWYzOTA3M2QwNmQ4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    public static final Block AMETHYST_TATER = createTaterBlock(Blocks.AMETHYST_BLOCK, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDg5Nzg2ODM4OSwKICAicHJvZmlsZUlkIiA6ICI4MmM2MDZjNWM2NTI0Yjc5OGI5MWExMmQzYTYxNjk3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3ROb3RvcmlvdXNOZW1vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EwOTcyZGJmNzEwNjdjMTdhMDFhNjEzMDU4NmE4ZjBhZjgwYjI2N2M0YzFjNzQwN2VlZTA1OTIxNGEwMmRhNTgiCiAgICB9CiAgfQp9");
    public static final Block PACKED_ICE_TATER = createTaterBlock(Blocks.PACKED_ICE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTI4NzQ0OTg4MiwKICAicHJvZmlsZUlkIiA6ICIzNmMxODk4ZjlhZGE0NjZlYjk0ZDFmZWFmMjQ0MTkxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5haWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVlZTQxZjRiYmMzODgxNWI0OTdhYjBiZmYxYTk4MWE3Y2QyMTIxYjUzOTY2MTY2MmRmNGYyODUxNTAwNzM3ZTMiCiAgICB9CiAgfQp9");
    public static final Block BLUE_ICE_TATER = createTaterBlock(Blocks.BLUE_ICE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMyMjI5NTMxNiwKICAicHJvZmlsZUlkIiA6ICJjNTBhZmE4YWJlYjk0ZTQ1OTRiZjFiNDI1YTk4MGYwMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUd29FQmFlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZiMDBlYzZjYjE5Mjg0NDhmZDcyYWRkNDM1MjY0M2E3ZDA0ZGMxZmI1NGNiYjM3ZDgwOTQ4NDRhODQ2NDY4MzAiCiAgICB9CiAgfQp9");
    public static final Block FLAME_TATER = createTaterBlock(ParticleTypes.FLAME, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzc4NjIwMTYxMSwKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY5YTBlNjFiMWViZjAzYTIzYTZlNTg4NWQxMzNjMDA1NWY3NzU1YzNiNjNlM2ZiMmFjNDc1YWFmNGQ4NzQ2NyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block PUZZLE_CUBE_TATER = createTaterBlock(ParticleTypes.FIREWORK, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTIzNDI4NjY1MiwKICAicHJvZmlsZUlkIiA6ICIzOWEzOTMzZWE4MjU0OGU3ODQwNzQ1YzBjNGY3MjU2ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkZW1pbmVjcmFmdGVybG9sIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQxZjhkYTVjMzQyZTc5OWJmYWUxNTRjMTY2MjdiNjE5MDkyM2VhZTI3NzM1ZWY3ZmZiZGViMWExMjFjODgxMWIiCiAgICB9CiAgfQp9");
    public static final Block CRATE_TATER = createTaterBlock(ParticleTypes.WAX_ON, "ewogICJ0aW1lc3RhbXAiIDogMTYzODkwODYyNjk0NywKICAicHJvZmlsZUlkIiA6ICJiYzRlZGZiNWYzNmM0OGE3YWM5ZjFhMzlkYzIzZjRmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4YWNhNjgwYjIyNDYxMzQwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzViN2QzNGYxNmVmOWEzNjE5NjRjZTc0MDViNmI0NjNmNjBjYzIxNGJkYjYyNWUwODk4NjljNTBmNTMzMmY2MWUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
    public static final Block TATER_OF_UNDYING = createTaterBlock(ParticleTypes.TOTEM_OF_UNDYING, "ewogICJ0aW1lc3RhbXAiIDogMTYzNzg2MDQ4MDI5NCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjUyNmQ5MzE0NzgyNWUyZGI0NDRhYWY0YTk0NjRiNjFhZDRlNGRlZmIwYWRmOTQ0YTIyNzU1NDNlZmM5MTkyYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block CRYING_OBSIDIAN_TATER = createTaterBlock(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, "ewogICJ0aW1lc3RhbXAiIDogMTYzODg4MzgyNzAwNiwKICAicHJvZmlsZUlkIiA6ICI5N2ViMDJkMWY5YmI0NjUwYmNmNzE2MTEzYjUzYjY4ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTQmNhY3R1cyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MGQxNTFhMmRhODNiNGEwZWM3M2JlMDhhNDJjNGJjODk2NGZmMWQzYWUyMTA2Y2MyOWI5Yjc5Y2ZjY2I4YjlmIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    public static final Block FLIPPED_TATER = createTaterBlock(ParticleTypes.DAMAGE_INDICATOR, "ewogICJ0aW1lc3RhbXAiIDogMTYxMDU1MjM5MTA3NywKICAicHJvZmlsZUlkIiA6ICI5MThhMDI5NTU5ZGQ0Y2U2YjE2ZjdhNWQ1M2VmYjQxMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWV2ZWxvcGVyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzljMWUzM2M0YjdlNmNiNThlNjk5YWViN2FlNDEyMzI5ZjM1Y2I0NDNlNTA3NDNjODg5NmVkMzZkZmI2YTM1ODgiCiAgICB9CiAgfQp9");
    public static final Block SANTA_HAT_TATER = createTaterBlock(ParticleTypes.SNOWFLAKE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDM3ODMyNzU2NywKICAicHJvZmlsZUlkIiA6ICJlMmVkYTM1YjMzZGU0M2UxOTVhZmRkNDgxNzQ4ZDlhOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDaGFsa19SaWNlR0kiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYwOWYzNTEwYjI5YmZhMTUwNGI3ZDNkNTNmNDY2ZTc2MjhhMWNlYmQxMDI1NzM2MDFkYjMzZTFjZWQ4OGM0ZSIKICAgIH0KICB9Cn0=");

    public static final Block SKELETATER = createTaterBlock(Blocks.BONE_BLOCK, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDcwMTgxODU1NCwKICAicHJvZmlsZUlkIiA6ICIzNmMxODk4ZjlhZGE0NjZlYjk0ZDFmZWFmMjQ0MTkxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJMdW5haWFuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y0NzJlMmZjMzZjN2Q4ZWY3Y2ZmOGNmYmE3MWU3ZTIzOGY4OWRlMDFlOGQ0NGMyMTcwM2YxYWM2ZDJjNDdmMSIKICAgIH0KICB9Cn0=");
    public static final Block WITHER_SKELETATER = createTaterBlock(Blocks.SOUL_SAND, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDcwMTkwMjk4NywKICAicHJvZmlsZUlkIiA6ICJjNjEwOTExMDhlOTQ0MWRhODQyZDA5MDVmMDAyOWVhOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkZVlvbm8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM3MzllMDNhYWY5OGYxZTk3MmUyMzEyMjQyM2Y4MjE2NGJjZWFkY2UzOTIyMWZhN2Y2OTA5ZDkwZWIwNTIyMyIKICAgIH0KICB9Cn0=");
    public static final Block ZOMBIE_TATER = createTaterBlock(Items.ROTTEN_FLESH, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDcwMTk0MDEwNSwKICAicHJvZmlsZUlkIiA6ICJjZGM5MzQ0NDAzODM0ZDdkYmRmOWUyMmVjZmM5MzBiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXdMb2JzdGVycyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NDk1ZDZhYTVlZjE4NTA2MmU4ZDlkMWJhMTcyMWQ1MTQ0ZTI0MTU1MjQ0ZjUzZWE0MTI5MDM4Zjk3N2EwNzM1IgogICAgfQogIH0KfQ==");
    public static final Block CREEPER_TATER = createTaterBlock(ParticleTypes.EXPLOSION, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDcwMjA3NjA5MiwKICAicHJvZmlsZUlkIiA6ICI0ZDEzZWUyZjViOWI0N2I2OGU2NzhhMjAxN2VmZTc1MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcmF5ZGVyZWsiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE0MjhiMTg2N2ExYjI1Y2ZjZTI0YWU4MjE5ODY2NTNiMWViOWRjMjUzNjMwYWI4MmI2OTVjMmNiYjZkMWU1MiIKICAgIH0KICB9Cn0=");
    public static final Block STEVE_TATER = createTaterBlock(ParticleTypes.HAPPY_VILLAGER, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDcwMjE3MDM2MCwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NlN2NjMDViZWU5OTNjZDcwMWE3NDI0MTViMDkxNTYzODFhNTRhYWE3YWQ1OTcxOTQxNzIyZTMyZGQ2ZWYzZjIiCiAgICB9CiAgfQp9");
    public static final Block ALEX_TATER = createTaterBlock(ParticleTypes.HAPPY_VILLAGER, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDcwMjE5Mzc5MCwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQWFkxIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UxZjdkYzkwOWIyZDZjNjc5ZmZiNGM2OWM4ZDdjOGVkMzljZjQ3YTgzMzMwMTEzZjhmZTQ1YWM0YjBhNDgzYzgiCiAgICB9CiAgfQp9");

    public static final Block TRANS_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xEE90AD)), // pink
        new Vec3f(Vec3d.unpackRgb(0x3CB0DA)), // blue
        new Vec3f(Vec3d.unpackRgb(0xCFD5D6)), // white
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQxMjI4NzMzOCwKICAicHJvZmlsZUlkIiA6ICJiN2ZkYmU2N2NkMDA0NjgzYjlmYTllM2UxNzczODI1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y3N2RiYzgwOWIyNTQ0NDkwMjNmYWMwZGQ0ZTBkOTEwMGI1YzQ0MDc3NDhiZTA4OWYwZTAwYzdlZjdhYjc2NCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block ASEXUAL_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0x16161B)), // black
        new Vec3f(Vec3d.unpackRgb(0x3F4548)), // gray
        new Vec3f(Vec3d.unpackRgb(0xCFD5D6)), // white
        new Vec3f(Vec3d.unpackRgb(0x7B2BAD)), // purple
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQ0NTY2NzYxMCwKICAicHJvZmlsZUlkIiA6ICJmODJmNTQ1MDIzZDA0MTFkYmVlYzU4YWI4Y2JlMTNjNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXNwb25kZW50cyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zOTAyODg3ZGM1NWQ0ZjczNmQwYjU2NmFkODEyZjI1NjExM2FhYTRhMzE4ZmZiODY1NjIzZmI1YTY3N2FlZjMyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    public static final Block BI_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xBE46B5)), // pink
        new Vec3f(Vec3d.unpackRgb(0x7B2BAD)), // purple
        new Vec3f(Vec3d.unpackRgb(0x353A9E)), // blue
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQ0NjEyNzQzOCwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ1MjZhNzJjYTViZTQyOTIwY2QzMTAyODBjMDNlMmM5ZTlhNzBjNTVhYTljYzFhMGM0ODM5NmQ1NTZmMWM3NWQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
    public static final Block GAY_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xA12823)), // red
        new Vec3f(Vec3d.unpackRgb(0xF17716)), // orange
        new Vec3f(Vec3d.unpackRgb(0xF9C629)), // yellow
        new Vec3f(Vec3d.unpackRgb(0x556E1C)), // green
        new Vec3f(Vec3d.unpackRgb(0x353A9E)), // blue
        new Vec3f(Vec3d.unpackRgb(0x7B2BAD)), // purple
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQ0NjM3NjgwOCwKICAicHJvZmlsZUlkIiA6ICIzZjM4YmViZGYwMWQ0MjNkYWI4MjczZjUwNGFiNGEyNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJjazM0Nzk0MjM1NzUzNzMxIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y5ZjQ0NmYyOTM5NmZmNDQ0ZDBlZjRmNTNhNzBjMjhhZmI2OWU1ZDFkYTAzN2MwM2MyNzdkMjM5MTdkYWNkZWQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
    public static final Block LESBIAN_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xA12823)), // red
        new Vec3f(Vec3d.unpackRgb(0xF17716)), // orange
        new Vec3f(Vec3d.unpackRgb(0xEAEDED)), // white
        new Vec3f(Vec3d.unpackRgb(0xEE90AD)), // pink
        new Vec3f(Vec3d.unpackRgb(0xBE46B5)), // magenta
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQ0NjU4MjIyMSwKICAicHJvZmlsZUlkIiA6ICJiNjM2OWQ0MzMwNTU0NGIzOWE5OTBhODYyNWY5MmEwNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb2JpbmhvXyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NDQ5Mjc0MGY0MGMxOWMzZTUyODcxY2RmNmNiZDU4NWU5ODBmYzdiNTBjYjBmYzk0OWJmYmU0NDAzMmE3ZGI3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    public static final Block NONBINARY_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xF9C629)), // yellow
        new Vec3f(Vec3d.unpackRgb(0x16161B)), // black
        new Vec3f(Vec3d.unpackRgb(0xCFD5D6)), // white
        new Vec3f(Vec3d.unpackRgb(0x7B2BAD)), // purple
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQ0NzAyNjQwNiwKICAicHJvZmlsZUlkIiA6ICJjNjEwOTExMDhlOTQ0MWRhODQyZDA5MDVmMDAyOWVhOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkZVlvbm8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA4NTRlNDczYmM3YTBhNjk1NmNiMTJkZjgwMjZkZTlmYzAwZmFlNDBjMDUwMmEzMTgyOTA4YmJiNTBjOWFhNSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block PAN_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xFA318C)), // pink
        new Vec3f(Vec3d.unpackRgb(0xFDD73B)), // yellow
        new Vec3f(Vec3d.unpackRgb(0x2394F9)), // blue
    }, "ewogICJ0aW1lc3RhbXAiIDogMTYzOTQ0NzE2NDMzOSwKICAicHJvZmlsZUlkIiA6ICJhYzYxZjQwZGJhNDE0YzkwOWU0NWJkMTgwMmY5MTYxYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmlmYW5pIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNmNzYxYmUxOGYwNzBhMDE2ZTRmNjFkMzdlYzEzYjIzMDMyYTU1MmRjZGI3MGE2N2Y4NTVjM2FiMmZhZTU0ZTAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==");
    public static final Block GENDERFLUID_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0xBE46B5)), // pink
        new Vec3f(Vec3d.unpackRgb(0xCFD5D6)), // white
        new Vec3f(Vec3d.unpackRgb(0x7B2BAD)), // purple
        new Vec3f(Vec3d.unpackRgb(0x16161B)), // black
        new Vec3f(Vec3d.unpackRgb(0x2394F9)), // blue
    }, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDYxOTIxMjYyMCwKICAicHJvZmlsZUlkIiA6ICJiNzQ3OWJhZTI5YzQ0YjIzYmE1NjI4MzM3OGYwZTNjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTeWxlZXgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmEwNjZjZGQ4ZDQ4NTAxZWI1MWVlYTFlM2U0MTdjMjVlZjUxYTA0Mjg0NzE0YmFhZDVhYjVkZTVjZDQyMjFiOCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
    public static final Block DEMISEXUAL_TATER = createColorPatternTaterBlock(new Vec3f[]{
        new Vec3f(Vec3d.unpackRgb(0x16161B)), // black
        new Vec3f(Vec3d.unpackRgb(0xCFD5D6)), // white
        new Vec3f(Vec3d.unpackRgb(0x7B2BAD)), // purple
        new Vec3f(Vec3d.unpackRgb(0x3F4548)), // gray
    }, "ewogICJ0aW1lc3RhbXAiIDogMTY0MDYyMDQyODY1MiwKICAicHJvZmlsZUlkIiA6ICJmODJmNTQ1MDIzZDA0MTFkYmVlYzU4YWI4Y2JlMTNjNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXNwb25kZW50cyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zMmI3Y2QyYzVkNzBjYWI0NzZjZTk1MWUyYzUyMGM5YjM1NzkyNTBhZDkwMDE2NGQ2YzIzMjFjN2Y0M2Q2ZGM3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");

    public static final Block WARDEN_TATER = createWardenTaterBlock("ewogICJ0aW1lc3RhbXAiIDogMTY0MDgxMTgxMjYwNCwKICAicHJvZmlsZUlkIiA6ICJkODAwZDI4MDlmNTE0ZjkxODk4YTU4MWYzODE0Yzc5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ0aGVCTFJ4eCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MmU0MTFhYTE1MDFjNzJkOTlkNzM4Y2IzOGUyNTBhMzk1YzY2MDRiOGJjY2M5ZjI5ZDdmMjZlOWNhY2Q4ZDZmIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    public static final Block DICE_TATER = createDiceTaterBlock();
    public static final Block TATEROID = createTateroidBlock(SoundEvents.BLOCK_NOTE_BLOCK_BELL);

    public static final Block WHITE_TATER = createColorTaterBlock(DyeColor.WHITE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzI2Njg3OCwKICAicHJvZmlsZUlkIiA6ICJhYzYxZjQwZGJhNDE0YzkwOWU0NWJkMTgwMmY5MTYxYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmlmYW5pIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzczZGFiMDUyZDMzZWU0NjdiYTdmYWM5YWEwZTMxNmRiOTYyZTNlN2FjNmRiYmZmMjM2NjY3NDM5ZTM0MDM5MmMiCiAgICB9CiAgfQp9");
    public static final Block ORANGE_TATER = createColorTaterBlock(DyeColor.ORANGE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzE4Nzk1MCwKICAicHJvZmlsZUlkIiA6ICJiN2ZkYmU2N2NkMDA0NjgzYjlmYTllM2UxNzczODI1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc1Yjg4MTI2ZGJkNGU4NjA2MDg5NjVjMDQ0ZDAwNjBhYzAzYzI2ZWJlYTFiNjUyNjQzZmUwMzczNGVhMWIxMmIiCiAgICB9CiAgfQp9");
    public static final Block MAGENTA_TATER = createColorTaterBlock(DyeColor.MAGENTA, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzE2OTQ5NiwKICAicHJvZmlsZUlkIiA6ICI1NjY3NWIyMjMyZjA0ZWUwODkxNzllOWM5MjA2Y2ZlOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVJbmRyYSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZWY0Y2Y0MGYwMmJjMTI5ZTM0ZjY2MGNlMzkyMzM4Nzg5NGI4ZjA4MTRmYzU4YzA5NzI2NjI5ZmE3YjFkYjY0IgogICAgfQogIH0KfQ==");
    public static final Block LIGHT_BLUE_TATER = createColorTaterBlock(DyeColor.LIGHT_BLUE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzExMzY0OCwKICAicHJvZmlsZUlkIiA6ICJjNzQ1Mzc4MDY5MzY0ODg2ODkwNzRkOTQ3ZjBlOTlmNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJjdWN1bWkwNyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNmY3ZTQ2NDFmYTRlZTRiYTkyNmViYWU0NjY5ZjIyZjI3NjA1MWIzNjI5ZjhkODljZTAxNWE5NWMxMTM3ZmIyIgogICAgfQogIH0KfQ==");
    public static final Block YELLOW_TATER = createColorTaterBlock(DyeColor.YELLOW, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzI4NTIxNiwKICAicHJvZmlsZUlkIiA6ICJjNzQ1Mzc4MDY5MzY0ODg2ODkwNzRkOTQ3ZjBlOTlmNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJjdWN1bWkwNyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81OTU2ZGYzZTg4ZjAxY2RkMjZkMWNhYTA3ZmFiNzIzYzNhM2RiMzE5YmExNjdhM2JlZDI4N2Q0NjE2MzVmMWI5IgogICAgfQogIH0KfQ==");
    public static final Block LIME_TATER = createColorTaterBlock(DyeColor.LIME, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzE0OTY4MywKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE2NjcxZjRjN2NlOGU0NjA5OWYzNjdmYzA1YzViNjEwODljODg3ZjQxNDVhZmYyMDc3YTFhN2QzNjMxZGQwNjMiCiAgICB9CiAgfQp9");
    public static final Block PINK_TATER = createColorTaterBlock(DyeColor.PINK, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzIwODA3OSwKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjNlZmViYmE0OTA2ZjlmMjYwYWFlODNmZWU3MzAxMjM3MDUyMTcyNmZhZGYwMTAxNDk4MTk3ZDcxNDNhNjRkZiIKICAgIH0KICB9Cn0=");
    public static final Block GRAY_TATER = createColorTaterBlock(DyeColor.GRAY, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzA3MzgxMywKICAicHJvZmlsZUlkIiA6ICIwYTUzMDU0MTM4YWI0YjIyOTVhMGNlZmJiMGU4MmFkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJQX0hpc2lybyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83ZTQ5MWI2MjgyZWNhMTJjOWVkY2RkZmIzYmFmNmU1ZmIwNTQ5Yzg5ZDM5MDg4NTY3NDE1NjE2YzkyZGZkNWYwIgogICAgfQogIH0KfQ==");
    public static final Block LIGHT_GRAY_TATER = createColorTaterBlock(DyeColor.LIGHT_GRAY, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzEyNzY2NCwKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1ZGY3M2NjMDI2MDQzZDA5YzU0Y2IxNWQwOTIzZTMxNDUwM2UxZDUxMzAyNmI1OWE1MzE3YjMxZGE0YzUyODkiCiAgICB9CiAgfQp9");
    public static final Block CYAN_TATER = createColorTaterBlock(DyeColor.CYAN, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzA1NTA0NiwKICAicHJvZmlsZUlkIiA6ICJjZGM5MzQ0NDAzODM0ZDdkYmRmOWUyMmVjZmM5MzBiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXdMb2JzdGVycyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ZWIzOTAzMjA1M2EzZjJlNWJkMWQxOWJmYWNkMjViNTI0YjE5Y2QwZTcwYTVhOTJhNjFhYzg4NDkwNGVjYjU0IgogICAgfQogIH0KfQ==");
    public static final Block PURPLE_TATER = createColorTaterBlock(DyeColor.PURPLE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzIyNjMwNiwKICAicHJvZmlsZUlkIiA6ICI0Mzk0NGU5NWZiNjI0NmJiOGIyYmQ5NDZlNWY5YjhhMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCbGFja0NvdWxkIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FiNjlmZmE2ODEzNTAwMWU4NzE0Yzc4NjE3ZTVhMWEwMTc3ODI3Y2JiZDg2NmViOTg0ZjUyNGYwMjNmODdmZTUiCiAgICB9CiAgfQp9");
    public static final Block BLUE_TATER = createColorTaterBlock(DyeColor.BLUE, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzAxMTE0MCwKICAicHJvZmlsZUlkIiA6ICJkOWQyZTg3ZjI1M2Q0NGI3YmYxMDc3OTZhZGRmODI2ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJIYWNrR2lybCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MThiZTcyZTk0MjkxZGUxY2MzZTNkMmUyZmE4YmJkNzk0MjJjZGNiMGNiNzBjZWRkZTdkYzljMWJiZWY1ZmI1IgogICAgfQogIH0KfQ==");
    public static final Block BROWN_TATER = createColorTaterBlock(DyeColor.BROWN, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzAzNzY1OCwKICAicHJvZmlsZUlkIiA6ICJlZThjNWMzMGY3NWU0N2QxOTBmOTllNjI5NDgyOGZjMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcGFya19QaGFudG9tIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RmOTQyZjdjMjRmNGMxMDM1M2RhOTc0YTkzOGFkNzVmOWE3Y2NkY2EyMzJhOTlkZTg3MGM1NjkxZDc1ZmQ3MGMiCiAgICB9CiAgfQp9");
    public static final Block GREEN_TATER = createColorTaterBlock(DyeColor.GREEN, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzA5MjYwMiwKICAicHJvZmlsZUlkIiA6ICI4MmM2MDZjNWM2NTI0Yjc5OGI5MWExMmQzYTYxNjk3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3ROb3RvcmlvdXNOZW1vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhYjk4YTRjNjllODE3NzcxZmVjMTdjMDhjN2UwMzE5NTJjMDVhNTIwMTVkY2VjYjA5YzkzNTc2NzQ1YzcxZTIiCiAgICB9CiAgfQp9");
    public static final Block RED_TATER = createColorTaterBlock(DyeColor.RED, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNzI0Nzc3OSwKICAicHJvZmlsZUlkIiA6ICIwYTUzMDU0MTM4YWI0YjIyOTVhMGNlZmJiMGU4MmFkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJQX0hpc2lybyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MWU0YmY2ZjdhMDI5NTY3ZDU5OGZmZjczYzNjNzZlMGNkZWE5NTZhN2ZmZjVlYTcyNzllYTRiZjQwYzk2OGMyIgogICAgfQogIH0KfQ==");
    public static final Block BLACK_TATER = createColorTaterBlock(DyeColor.BLACK, "ewogICJ0aW1lc3RhbXAiIDogMTY0MTMwNjk4NTY5MCwKICAicHJvZmlsZUlkIiA6ICIzM2ViZDMyYmIzMzk0YWQ5YWM2NzBjOTZjNTQ5YmE3ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJEYW5ub0JhbmFubm9YRCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81N2E3Y2FhNDRjZWRmZjkyNWQyM2NkNWQzZjYyYmMwNmU4M2QzNDg1NTUxZGNjNGY5ZGIyZGE3ZDlmOGE5Njk0IgogICAgfQogIH0KfQ==");

    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD_ENTITY = FabricBlockEntityTypeBuilder.create(LaunchPadBlockEntity::new, GOLD_LAUNCH_PAD, IRON_LAUNCH_PAD).build(null);
    public static final BlockEntityType<TateroidBlockEntity> TATEROID_ENTITY = FabricBlockEntityTypeBuilder.create(TateroidBlockEntity::new, TATEROID).build(null);

    private static Block createSimple(Block virtual) {
        return new SimplePolymerBlock(AbstractBlock.Settings.copy(virtual).strength(100), virtual);
    }

    private static Block createTaterBlock(ParticleEffect effect, String texture) {
        return new TinyPotatoBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), effect, texture);
    }

    private static Block createTaterBlock(Block particleBlock, String texture) {
        return new TinyPotatoBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), particleBlock, texture);
    }

    private static Block createTaterBlock(Item particleItem, String texture) {
        return new TinyPotatoBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), particleItem, texture);
    }
  
    private static Block createColorPatternTaterBlock(Vec3f[] pattern, String texture) {
        return new ColorPatternTaterBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), pattern, texture);
    }

    private static Block createWardenTaterBlock(String texture) {
        return new WardenTaterBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), texture);
    }

    private static Block createDiceTaterBlock() {
        return new DiceTaterBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100));
    }

    private static Block createTateroidBlock(SoundEvent defaultSound) {
        return new TateroidBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), defaultSound);
    }

    private static Block createColorTaterBlock(DyeColor color, String texture) {
        return new ColorTaterBlock(AbstractBlock.Settings.of(Material.SOLID_ORGANIC).strength(100), color, texture);
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
        register("puzzle_cube_tater", PUZZLE_CUBE_TATER);
        register("tuff_tater", TUFF_TATER);
        register("dripstone_tater", DRIPSTONE_TATER);
        register("amethyst_tater", AMETHYST_TATER);
        register("packed_ice_tater", PACKED_ICE_TATER);
        register("blue_ice_tater", BLUE_ICE_TATER);
        register("flame_tater", FLAME_TATER);
        register("crate_tater", CRATE_TATER);
        register("tater_of_undying", TATER_OF_UNDYING);
        register("crying_obsidian_tater", CRYING_OBSIDIAN_TATER);
        register("trans_tater", TRANS_TATER);
        register("asexual_tater", ASEXUAL_TATER);
        register("bi_tater", BI_TATER);
        register("gay_tater", GAY_TATER);
        register("lesbian_tater", LESBIAN_TATER);
        register("nonbinary_tater", NONBINARY_TATER);
        register("pan_tater", PAN_TATER);
        register("flipped_tater", FLIPPED_TATER);
        register("santa_hat_tater", SANTA_HAT_TATER);
        register("genderfluid_tater", GENDERFLUID_TATER);
        register("demisexual_tater", DEMISEXUAL_TATER);

        register("warden_tater", WARDEN_TATER);
        register("dice_tater", DICE_TATER);
        register("tateroid", TATEROID);

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
        
        registerBlockEntity("launch_pad", LAUNCH_PAD_ENTITY);
        registerBlockEntity("tateroid", TATEROID_ENTITY);
    }

    private static <T extends Block> T register(String id, T block) {
        return Registry.register(Registry.BLOCK, NucleoidExtras.identifier(id), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, BlockEntityType<T> type) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, NucleoidExtras.identifier(id), type);
        PolymerBlockUtils.registerBlockEntity(type);
        return type;
    }
}
