package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.extras.util.SkinEncoder;
import xyz.nucleoid.packettweaker.PacketContext;

public class DiceTaterBlock extends CubicPotatoBlock {
    private static final int ROLLING_FACE = 0;
    private static final int MAX_FACE = 6;
    private static final int ROLLING_TICKS = 8;
    private static final IntegerProperty FACE = IntegerProperty.create("face", ROLLING_FACE, MAX_FACE);
    private static final String[] TEXTURES = {
        SkinEncoder.encode("b4d4126574c3dcb9847547f29f04e5df6cf0fc6d862b4abe75926f359d5d6a91"),
        SkinEncoder.encode("59b568e3d4eb5309e3660f4acfc04ecaa84825d2fe4a1312591d128b26859eaf"),
        SkinEncoder.encode("753e1961ebc1de62ddb1316dba84348a7aa8c07c322b8f4e8f6f58f55cf28060"),
        SkinEncoder.encode("57eb8f0e36d98818caee1067625d8eaa5d766f51d0f7fa75595a48f430345986"),
        SkinEncoder.encode("613731d224be1fd184ff759a5e17a896c3c26d35057c6d9989f07ae7ffe47120"),
        SkinEncoder.encode("a14b08078bf97e82bb7056b7a4f820626a42fe72395cbbf33a0eea72af0e7a12"),
        SkinEncoder.encode("9c40bf70f1648b7ee438a6a22904228ab5fbbd4926af30ae8ade4df01b8d7413"),
    };

    public DiceTaterBlock(Properties settings) {
        super(settings, ParticleTypes.POOF, TEXTURES[6]);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACE, 1));
    }

    private boolean isRolling(BlockState state) {
        return state.getValue(FACE) == ROLLING_FACE;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (this.isRolling(state)) {
            return InteractionResult.FAIL;
        }

        if (world instanceof ServerLevel) {
            world.setBlockAndUpdate(pos, state.setValue(FACE, ROLLING_FACE));
            world.scheduleTick(pos, this, ROLLING_TICKS);

            float pitch = 1.6f + world.getRandom().nextFloat() * 0.4f;
            world.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1, pitch);
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (this.isRolling(state)) {
            int face = world.getRandom().nextInt(MAX_FACE) + 1;
            world.setBlockAndUpdate(pos, state.setValue(FACE, face));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        int face = state.getValue(FACE);
        return Mth.floor(face / 6f * 15f);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACE);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        int face = state.getValue(FACE);
        return TEXTURES[face];
    }
}
