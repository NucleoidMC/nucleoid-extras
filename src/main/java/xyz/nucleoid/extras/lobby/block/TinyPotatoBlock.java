package xyz.nucleoid.extras.lobby.block;

import java.util.ArrayList;
import java.util.List;

import eu.pb4.polymer.api.block.PolymerHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
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

public class TinyPotatoBlock extends Block implements PolymerHeadBlock {

    public static final List<TinyPotatoBlock> TATERS = new ArrayList<>();

    private final String texture;
    private final ParticleEffect particleEffect;
    private final int particleRate;

    public TinyPotatoBlock(Settings settings, ParticleEffect particleEffect, String texture, int particleRate) {
        super(settings);
        this.particleEffect = particleEffect;
        this.texture = texture;
        this.particleRate = particleRate;

        TATERS.add(this);
    }

    public TinyPotatoBlock(Settings settings, ParticleEffect particleEffect, String texture) {
        this(settings, particleEffect, texture, 2);
    }

    public TinyPotatoBlock(Settings settings, BlockState particleState, String texture) {
        this(settings, new BlockStateParticleEffect(ParticleTypes.BLOCK, particleState), texture);
    }

    public TinyPotatoBlock(Settings settings, Block particleBlock, String texture) {
        this(settings, particleBlock.getDefaultState(), texture);
    }

    public TinyPotatoBlock(Settings settings, ItemStack particleStack, String texture) {
        this(settings, new ItemStackParticleEffect(ParticleTypes.ITEM, particleStack), texture);
    }

    public TinyPotatoBlock(Settings settings, Item particleItem, String texture) {
        this(settings, new ItemStack(particleItem), texture);
    }

    public ParticleEffect getParticleEffect(int time) {
        return this.particleEffect;
    }

    public ParticleEffect getBlockParticleEffect(BlockState state, ServerWorld world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return this.getParticleEffect(world.getServer().getTicks());
    }

    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0.5, 0.5, 0.5, 0.2);
        }
    }

    public ParticleEffect getPlayerParticleEffect(ServerPlayerEntity player) {
        return this.getParticleEffect(player.getServer().getTicks());
    }

    public int getBlockParticleChance() {
        return 1;
    }

    public int getPlayerParticleRate(ServerPlayerEntity player) {
        return particleRate;
    }

    public void spawnPlayerParticles(ServerPlayerEntity player) {
        Box box = player.getBoundingBox();

        double deltaX = box.getXLength() / 2d;
        double deltaY = box.getYLength() / 2d;
        double deltaZ = box.getZLength() / 2d;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.getWorld().spawnParticles(particleEffect, x, y, z, 1, deltaX, deltaY, deltaZ, 0.2);
        }
    }

    /**
     * {@return whether the block should be removed after the tater is collected}
     */
    public boolean isFickle() {
        return false;
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
            ParticleEffect particleEffect = this.getBlockParticleEffect(state, serverWorld, pos, player, hand, hit);
            this.spawnBlockParticles(serverWorld, pos, particleEffect);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public String getPolymerSkinValue(BlockState state) {
        return this.texture;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.ROTATION, MathHelper.floor((double)(ctx.getPlayerYaw() * 16.0F / 360.0F) + 0.5D) & 15);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return PolymerHeadBlock.super.getPolymerBlock();
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return PolymerHeadBlock.super.getPolymerBlockState(state).with(Properties.ROTATION, state.get(Properties.ROTATION));
    }
}
