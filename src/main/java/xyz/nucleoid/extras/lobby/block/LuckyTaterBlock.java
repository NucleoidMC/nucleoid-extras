package xyz.nucleoid.extras.lobby.block;

import java.util.Random;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import xyz.nucleoid.extras.NucleoidExtras;

public class LuckyTaterBlock extends TinyPotatoBlock {
    private static final BooleanProperty COOLDOWN = BooleanProperty.of("cooldown");
    private static final int COOLDOWN_TICKS = SharedConstants.TICKS_PER_MINUTE * 30;

    private static final Identifier LUCKY_TATER_DROPS_ID = NucleoidExtras.identifier("lucky_tater_drops");
    private static final TagKey<Block> LUCKY_TATER_DROPS = TagKey.of(Registry.BLOCK_KEY, LUCKY_TATER_DROPS_ID);

    private final String cooldownTexture;

    public LuckyTaterBlock(Settings settings, String texture, String cooldownTexture) {
        super(settings, (ParticleEffect) null, texture);
        this.cooldownTexture = cooldownTexture;

        this.setDefaultState(this.stateManager.getDefaultState().with(COOLDOWN, false));
    }

    @Override
    public ParticleEffect getPlayerParticleEffect(ServerPlayerEntity player) {
        int fromColor = LuckyTaterBlock.getRandomColor(player.getRandom());
        int toColor = LuckyTaterBlock.getRandomColor(player.getRandom());

        int scale = player.getRandom().nextInt(3);
        return new DustColorTransitionParticleEffect(new Vec3f(Vec3d.unpackRgb(fromColor)), new Vec3f(Vec3d.unpackRgb(toColor)), scale);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND || this.isCoolingDown(state)) {
            return ActionResult.FAIL;
        }

        var taters = Registry.BLOCK.getEntryList(LUCKY_TATER_DROPS);
        if (world instanceof ServerWorld serverWorld && taters.isPresent()) {
            Block drop = taters.get().getRandom(world.getRandom()).get().value();
            if (drop instanceof TinyPotatoBlock taterDrop) {
                BlockPos dropPos = this.getDropPos(serverWorld, state, pos);
                if (dropPos != null) {
                    BlockState dropState = drop.getDefaultState();
                    if (dropState.contains(Properties.ROTATION)) {
                        dropState = dropState.with(Properties.ROTATION, state.get(Properties.ROTATION));
                    }

                    world.setBlockState(dropPos, dropState);
                    
                    // Spawn particles
                    ParticleEffect particleEffect = taterDrop.getBlockParticleEffect(taterDrop.getDefaultState(), serverWorld, pos, player, hand, hit);
                    this.spawnBlockParticles(serverWorld, pos, particleEffect);

                    // Play sound
                    float pitch = 0.5f + world.getRandom().nextFloat() * 0.4f;
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1, pitch);

                    // Start cooldown
                    world.setBlockState(pos, state.with(COOLDOWN, true));
                    world.createAndScheduleBlockTick(pos, this, COOLDOWN_TICKS);
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    private BlockPos getDropPos(ServerWorld world, BlockState state, BlockPos pos) {
        BlockPos.Mutable dropPos = pos.mutableCopy();
        dropPos.move(Direction.DOWN);

        int rotation = state.get(Properties.ROTATION);
        dropPos.move(Direction.fromRotation(rotation * 22.5).getOpposite());

        for (int i = 0; i < 3; i++) {
            BlockState dropState = world.getBlockState(dropPos);
            if (dropState.getBlock() instanceof TinyPotatoBlock) {
                return null;
            } else if (dropState.isAir()) {
                return dropPos;
            }

            dropPos.move(Direction.UP);
        }

        return null;
    }

    private boolean isCoolingDown(BlockState state) {
        return state.get(COOLDOWN);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.isCoolingDown(state)) {
            world.setBlockState(pos, state.with(COOLDOWN, false));
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(COOLDOWN) ? 15 : 0;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COOLDOWN);
    }

    @Override
    public String getPolymerSkinValue(BlockState state) {
        return state.get(COOLDOWN) ? this.cooldownTexture : super.getPolymerSkinValue(state);
    }

    private static int getRandomColor(Random random) {
        return random.nextInt() * 0xFFFFFF;
    }
}
