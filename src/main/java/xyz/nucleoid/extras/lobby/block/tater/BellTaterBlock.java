package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

public class BellTaterBlock extends CubicPotatoBlock implements EntityBlock {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public BellTaterBlock(Properties settings, String texture) {
		super(settings, ParticleTypes.NOTE, texture);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, Orientation wireOrientation, boolean notify) {
		boolean bl = world.hasNeighborSignal(pos);
		if (bl != state.getValue(POWERED)) {
			if (bl) {
				this.ring(world, pos, null);
			}
			world.setBlock(pos, state.setValue(POWERED, bl), Block.UPDATE_ALL);
		}
	}

	@Override
	public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
		Entity entity = projectile.getOwner();
		Player playerEntity = entity instanceof Player ? (Player)entity : null;
		this.ring(world, hit, playerEntity);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		super.useWithoutItem(state, world, pos, player, hit);
		return this.ring(world, hit, player) ? InteractionResult.SUCCESS_SERVER : InteractionResult.PASS;
	}

	public boolean ring(Level world, BlockHitResult hitResult, @Nullable Player player) {
		Direction direction = hitResult.getDirection();
		BlockPos blockPos = hitResult.getBlockPos();
		boolean bl32 = this.ring(player, world, blockPos, direction);
		if (bl32 && player != null) {
			player.awardStat(Stats.BELL_RING);
		}
		return true;
	}

	public boolean ring(Level world, BlockPos pos, @Nullable Direction direction) {
		return this.ring(null, world, pos, direction);
	}

	public boolean ring(@Nullable Entity entity, Level world, BlockPos pos, @Nullable Direction direction) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!world.isClientSide() && blockEntity instanceof BellTaterBlockEntity bellTaterBlockEntity) {
			if (direction == null) {
				int rotation = world.getBlockState(pos).getValue(BlockStateProperties.ROTATION_16);
				direction = Direction.fromYRot(rotation * 22.5);
			}
			bellTaterBlockEntity.activate(direction);
			world.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0f, 1.0f);
			world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BellTaterBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return BlockWithEntityAccessor.validateTicker(type, NEBlocks.BELL_TATER_ENTITY, world.isClientSide() ? BellTaterBlockEntity::clientTick : BellTaterBlockEntity::serverTick);
	}
}
