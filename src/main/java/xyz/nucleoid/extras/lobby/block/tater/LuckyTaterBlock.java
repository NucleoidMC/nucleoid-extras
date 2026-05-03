package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.extras.tag.NEBlockTags;
import xyz.nucleoid.extras.util.SkinEncoder;
import xyz.nucleoid.packettweaker.PacketContext;

public class LuckyTaterBlock extends CubicPotatoBlock {
    private static final EnumProperty<LuckyTaterPhase> PHASE = EnumProperty.create("phase", LuckyTaterPhase.class);

    private static final int COURAGE_TICKS = 5;
    private static final int COOLDOWN_TICKS = SharedConstants.TICKS_PER_MINUTE * 30;

    private final String cooldownTexture;

    public LuckyTaterBlock(Properties settings, String texture, String cooldownTexture) {
        super(settings, (ParticleOptions) null, texture);
        this.cooldownTexture = SkinEncoder.encode(cooldownTexture);

        this.registerDefaultState(this.stateDefinition.any().setValue(PHASE, LuckyTaterPhase.READY));
    }

    @Override
    public ParticleOptions getPlayerParticleEffect(ServerPlayer player) {
        int fromColor = LuckyTaterBlock.getRandomColor(player.getRandom());
        int toColor = LuckyTaterBlock.getRandomColor(player.getRandom());

        int scale = player.getRandom().nextInt(3);
        return new DustColorTransitionOptions(fromColor, toColor, scale);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        LuckyTaterPhase phase = state.getValue(PHASE);

        if (phase != LuckyTaterPhase.READY) {
            return InteractionResult.FAIL;
        }

        if (world instanceof ServerLevel serverWorld) {
            LuckyTaterDropPos dropPos = this.getDropPos(serverWorld, state, pos);

            if (dropPos instanceof LuckyTaterDropPos.Blocked) {
                world.setBlockAndUpdate(pos, state.setValue(PHASE, LuckyTaterPhase.BUILDING_COURAGE));
                world.scheduleTick(pos, this, COURAGE_TICKS);
            } else {
                Block drop = this.getDrop(serverWorld);

                if (drop instanceof CubicPotatoBlock taterDrop && dropPos instanceof LuckyTaterDropPos.Allowed allowed) {
                    BlockState dropState = drop.defaultBlockState();
                    if (dropState.hasProperty(BlockStateProperties.ROTATION_16)) {
                        dropState = dropState.setValue(BlockStateProperties.ROTATION_16, state.getValue(BlockStateProperties.ROTATION_16));
                    }

                    world.setBlockAndUpdate(allowed.pos(), dropState);

                    // Spawn particles
                    ParticleOptions particleEffect = taterDrop.getBlockParticleEffect(taterDrop.defaultBlockState(), serverWorld, pos, player, hit);
                    this.spawnBlockParticles(serverWorld, pos, particleEffect);

                    // Play sound
                    float pitch = 0.5f + world.getRandom().nextFloat() * 0.4f;
                    world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1, pitch);

                    // Start cooldown
                    world.setBlockAndUpdate(pos, state.setValue(PHASE, LuckyTaterPhase.COOLDOWN));
                    world.scheduleTick(pos, this, COOLDOWN_TICKS);
                }
            }
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    private Block getDrop(ServerLevel world) {
        var drops = world.registryAccess()
                .lookupOrThrow(Registries.BLOCK)
                .get(NEBlockTags.LUCKY_TATER_DROPS);

        if (drops.isEmpty()) {
            return null;
        }

        var builder = WeightedList.<Block>builder();

        for (Holder<Block> entry : drops.get()) {
            Block block = entry.value();
            int weight = block instanceof LuckyTaterDrop drop ? drop.getWeight() : 1;

            builder.add(block, weight);
        }

        return builder
            .build()
            .getRandom(world.getRandom())
            .orElse(null);
    }

    private LuckyTaterDropPos getDropPos(ServerLevel world, BlockState state, BlockPos pos) {
        BlockPos.MutableBlockPos dropPos = pos.mutable();
        dropPos.move(Direction.DOWN);

        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        dropPos.move(Direction.fromYRot(rotation * 22.5).getOpposite());

        for (int i = 0; i < 3; i++) {
            BlockState dropState = world.getBlockState(dropPos);
            if (dropState.getBlock() instanceof CubicPotatoBlock) {
                return new LuckyTaterDropPos.Blocked(dropPos);
            } else if (dropState.isAir()) {
                return new LuckyTaterDropPos.Allowed(dropPos);
            }

            dropPos.move(Direction.UP);
        }

        return LuckyTaterDropPos.None.INSTANCE;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        LuckyTaterPhase phase = state.getValue(PHASE);

        if (phase == LuckyTaterPhase.BUILDING_COURAGE || phase == LuckyTaterPhase.COOLDOWN) {
            if (phase == LuckyTaterPhase.BUILDING_COURAGE) {
                LuckyTaterDropPos dropPos = this.getDropPos(world, state, pos);

                if (dropPos instanceof LuckyTaterDropPos.Blocked blocked) {
                    world.destroyBlock(blocked.pos(), false);
                }
            }

            world.setBlockAndUpdate(pos, state.setValue(PHASE, LuckyTaterPhase.READY));
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction) {
        return state.getValue(PHASE).getComparatorOutput();
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PHASE);
    }

    @Override
    public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
        return state.getValue(PHASE) == LuckyTaterPhase.COOLDOWN ? this.cooldownTexture : super.getPolymerSkinValue(state, pos, context);
    }

    private static int getRandomColor(RandomSource random) {
        return random.nextInt() * 0xFFFFFF;
    }
}
