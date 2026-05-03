package xyz.nucleoid.extras.lobby.block.tater;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import xyz.nucleoid.extras.util.SkinEncoder;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class CubicPotatoBlock extends TinyPotatoBlock implements PolymerHeadBlock {
    protected static final List<CubicPotatoBlock> CUBIC_TATERS = new ArrayList<>();

    public CubicPotatoBlock(Properties settings, ParticleOptions particleEffect, String texture, int particleRate) {
        super(settings, texture, particleEffect, particleRate);
        CUBIC_TATERS.add(this);
    }

    public CubicPotatoBlock(Properties settings, ParticleOptions particleEffect, String texture) {
        this(settings, particleEffect, texture, 2);
    }

    public CubicPotatoBlock(Properties settings, BlockState particleState, String texture) {
        this(settings, new BlockParticleOption(ParticleTypes.BLOCK, particleState), texture);
    }

    public CubicPotatoBlock(Properties settings, Block particleBlock, String texture) {
        this(settings, particleBlock.defaultBlockState(), texture);
    }

    public CubicPotatoBlock(Properties settings, ItemStack particleStack, String texture) {
        this(settings, new ItemParticleOption(ParticleTypes.ITEM, particleStack), texture);
    }

    public CubicPotatoBlock(Properties settings, Item particleItem, String texture) {
        this(settings, new ItemStack(particleItem), texture);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        return this.getItemTexture();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, Mth.floor(RotationSegment.convertToSegment(ctx.getRotation())));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.PLAYER_HEAD.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, state.getValue(BlockStateProperties.ROTATION_16));
    }
}
