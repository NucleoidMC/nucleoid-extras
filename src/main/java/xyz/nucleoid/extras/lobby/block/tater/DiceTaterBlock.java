package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import xyz.nucleoid.extras.util.SkinEncoder;

public class DiceTaterBlock extends CubicPotatoBlock {
    private static final int ROLLING_FACE = 0;
    private static final int MAX_FACE = 6;
    private static final int ROLLING_TICKS = 8;
    private static final IntProperty FACE = IntProperty.of("face", ROLLING_FACE, MAX_FACE);
    private static final String[] TEXTURES = {
        SkinEncoder.encode("b4d4126574c3dcb9847547f29f04e5df6cf0fc6d862b4abe75926f359d5d6a91"),
        SkinEncoder.encode("59b568e3d4eb5309e3660f4acfc04ecaa84825d2fe4a1312591d128b26859eaf"),
        SkinEncoder.encode("753e1961ebc1de62ddb1316dba84348a7aa8c07c322b8f4e8f6f58f55cf28060"),
        SkinEncoder.encode("57eb8f0e36d98818caee1067625d8eaa5d766f51d0f7fa75595a48f430345986"),
        SkinEncoder.encode("613731d224be1fd184ff759a5e17a896c3c26d35057c6d9989f07ae7ffe47120"),
        SkinEncoder.encode("a14b08078bf97e82bb7056b7a4f820626a42fe72395cbbf33a0eea72af0e7a12"),
        SkinEncoder.encode("9c40bf70f1648b7ee438a6a22904228ab5fbbd4926af30ae8ade4df01b8d7413"),
    };

    public DiceTaterBlock(Settings settings) {
        super(settings, ParticleTypes.POOF, TEXTURES[6]);

        this.setDefaultState(this.stateManager.getDefaultState().with(FACE, 1));
    }

    private boolean isRolling(BlockState state) {
        return state.get(FACE) == ROLLING_FACE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND || this.isRolling(state)) {
            return ActionResult.FAIL;
        }

        if (world instanceof ServerWorld) {
            world.setBlockState(pos, state.with(FACE, ROLLING_FACE));
            world.scheduleBlockTick(pos, this, ROLLING_TICKS);

            float pitch = 1.6f + world.getRandom().nextFloat() * 0.4f;
            world.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1, pitch);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.isRolling(state)) {
            int face = world.getRandom().nextInt(MAX_FACE) + 1;
            world.setBlockState(pos, state.with(FACE, face));
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        int face = state.get(FACE);
        return MathHelper.floor(face / 6f * 15f);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACE);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, ServerPlayerEntity player) {
        int face = state.get(FACE);
        return TEXTURES[face];
    }
}
