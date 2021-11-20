package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.block.VirtualHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TinyPotatoBlock extends Block implements VirtualHeadBlock {

    private final String texture;
    private final ParticleEffect particleEffect;

    public TinyPotatoBlock(Settings settings, ParticleEffect particleEffect, String texture) {
        super(settings);
        this.particleEffect = particleEffect;
        this.texture = texture;
    }

    public TinyPotatoBlock(Settings settings, BlockState particleState, String texture) {
        this(settings, new BlockStateParticleEffect(ParticleTypes.BLOCK, particleState), texture);
    }

    public TinyPotatoBlock(Settings settings, Block particleBlock, String texture) {
        this(settings, particleBlock.getDefaultState(), texture);
    }

    public void spawnPlayerParticles(ServerPlayerEntity player) {
        Box box = player.getBoundingBox();

        double deltaX = box.getXLength() / 2d;
        double deltaY = box.getYLength() / 2d;
        double deltaZ = box.getZLength() / 2d;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        player.getServerWorld().spawnParticles(this.particleEffect, x, y, z, 1, deltaX, deltaY, deltaZ, 0.2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.ROTATION);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND) {
            return ActionResult.FAIL;
        }

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(this.particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0.5, 0.5, 0.5, 0.2);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public String getVirtualHeadSkin(BlockState state) {
        return this.texture;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.ROTATION, MathHelper.floor((double)(ctx.getPlayerYaw() * 16.0F / 360.0F) + 0.5D) & 15);
    }

    @Override
    public BlockState getVirtualBlockState(BlockState state) {
        return VirtualHeadBlock.super.getVirtualBlockState(state).with(Properties.ROTATION, state.get(Properties.ROTATION));
    }
}
