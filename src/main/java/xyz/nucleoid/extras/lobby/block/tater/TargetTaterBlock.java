package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetTaterBlock extends CubicPotatoBlock {
	private static final IntegerProperty POWER = BlockStateProperties.POWER;
	private static final int RECOVERABLE_POWER_DELAY = 20;
	private static final int REGULAR_POWER_DELAY = 8;

	public TargetTaterBlock(Properties settings, String texture) {
		super(settings, Blocks.TARGET, texture);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
		int power = TargetTaterBlock.trigger(world, state, hit, projectile);
		Entity entity = projectile.getOwner();
		if (entity instanceof ServerPlayer player) {
			player.awardStat(Stats.TARGET_HIT);
			CriteriaTriggers.TARGET_BLOCK_HIT.trigger(player, projectile, hit.getLocation(), power);
		}
	}

	private static int trigger(LevelAccessor world, BlockState state, BlockHitResult hitResult, Entity entity) {
		int power = TargetTaterBlock.calculatePower(hitResult, hitResult.getLocation());
		int delay = entity instanceof AbstractArrow ? RECOVERABLE_POWER_DELAY : REGULAR_POWER_DELAY;
		if (!world.getBlockTicks().hasScheduledTick(hitResult.getBlockPos(), state.getBlock())) {
			TargetTaterBlock.setPower(world, state, power, hitResult.getBlockPos(), delay);
		}
		return power;
	}

	private static int calculatePower(BlockHitResult hitResult, Vec3 pos) {
		Direction direction = hitResult.getDirection();
		double x = Math.abs(Mth.frac(pos.x) - 0.5);
		double y = Math.abs(Mth.frac(pos.y) - 0.5);
		double z = Math.abs(Mth.frac(pos.z) - 0.5);
		Direction.Axis axis = direction.getAxis();
		double g = axis == Direction.Axis.Y ? Math.max(x, z) : (axis == Direction.Axis.Z ? Math.max(x, y) : Math.max(y, z));
		return Math.max(1, Mth.ceil(15.0 * Mth.clamp((0.5 - g) / 0.5, 0.0, 1.0)));
	}

	private static void setPower(LevelAccessor world, BlockState state, int power, BlockPos pos, int delay) {
		world.setBlock(pos, state.setValue(POWER, power), Block.UPDATE_ALL);
		world.scheduleTick(pos, state.getBlock(), delay);
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (state.getValue(POWER) != 0) {
			world.setBlock(pos, state.setValue(POWER, 0), Block.UPDATE_ALL);
		}
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.getValue(POWER);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWER);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
		if (world.isClientSide() || state.is(oldState.getBlock())) {
			return;
		}
		if (state.getValue(POWER) > 0 && !world.getBlockTicks().hasScheduledTick(pos, this)) {
			world.setBlock(pos, state.setValue(POWER, 0), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		}
	}
}
