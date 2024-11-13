package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationPropertyHelper;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawner;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public class CubicPotatoBlock extends TinyPotatoBlock implements PolymerHeadBlock {
    public static final MapCodec<CubicPotatoBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
                createSettingsCodec(),
                TaterParticleSpawner.CODEC.fieldOf("particle_spawner").forGetter(CubicPotatoBlock::getParticleSpawner),
                Codec.STRING.fieldOf("texture").forGetter(CubicPotatoBlock::getItemTexture)
        ).apply(instance, CubicPotatoBlock::new)
    );

    protected static final List<CubicPotatoBlock> CUBIC_TATERS = new ArrayList<>();

    public CubicPotatoBlock(Settings settings, TaterParticleSpawner particleSpawner, String texture) {
        super(settings, particleSpawner, texture);
        CUBIC_TATERS.add(this);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        return this.getItemTexture();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.ROTATION);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.ROTATION, MathHelper.floor(RotationPropertyHelper.fromYaw(ctx.getPlayerYaw())));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.PLAYER_HEAD.getDefaultState().with(Properties.ROTATION, state.get(Properties.ROTATION));
    }

    @Override
    public MapCodec<? extends CubicPotatoBlock> getCodec() {
        return CODEC;
    }
}
