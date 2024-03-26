package xyz.nucleoid.extras.lobby.block.tater;

import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BellTaterBlock extends CubicPotatoBlock implements BlockEntityProvider {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public BellTaterBlock(Settings settings, String texture) {
		super(settings, ParticleTypes.NOTE, texture);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != state.get(POWERED)) {
			if (bl) {
				this.ring(world, pos, null);
			}
			world.setBlockState(pos, state.with(POWERED, bl), Block.NOTIFY_ALL);
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		Entity entity = projectile.getOwner();
		PlayerEntity playerEntity = entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
		this.ring(world, hit, playerEntity);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		super.onUse(state, world, pos, player, hand, hit);
		return this.ring(world, hit, player) ? ActionResult.success(world.isClient) : ActionResult.PASS;
	}

	public boolean ring(World world, BlockHitResult hitResult, @Nullable PlayerEntity player) {
		Direction direction = hitResult.getSide();
		BlockPos blockPos = hitResult.getBlockPos();
		boolean bl32 = this.ring(player, world, blockPos, direction);
		if (bl32 && player != null) {
			player.incrementStat(Stats.BELL_RING);
		}
		return true;
	}

	public boolean ring(World world, BlockPos pos, @Nullable Direction direction) {
		return this.ring(null, world, pos, direction);
	}

	public boolean ring(@Nullable Entity entity, World world, BlockPos pos, @Nullable Direction direction) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!world.isClient && blockEntity instanceof BellTaterBlockEntity bellTaterBlockEntity) {
			if (direction == null) {
				int rotation = world.getBlockState(pos).get(Properties.ROTATION);
				direction = Direction.fromRotation(rotation * 22.5);
			}
			bellTaterBlockEntity.activate(direction);
			world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0f, 1.0f);
			world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}
		return false;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(POWERED);
	}

	@Override
	@Nullable
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BellTaterBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return BlockWithEntityAccessor.checkType(type, NEBlocks.BELL_TATER_ENTITY, world.isClient ? BellTaterBlockEntity::clientTick : BellTaterBlockEntity::serverTick);
	}
}
