package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TargetTaterBlock extends CubicPotatoBlock {
	private static final IntProperty POWER = Properties.POWER;
	private static final int RECOVERABLE_POWER_DELAY = 20;
	private static final int REGULAR_POWER_DELAY = 8;

	public TargetTaterBlock(Settings settings, String texture) {
		super(settings, Blocks.TARGET, texture);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, 0));
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		int power = TargetTaterBlock.trigger(world, state, hit, projectile);
		Entity entity = projectile.getOwner();
		if (entity instanceof ServerPlayerEntity player) {
			player.incrementStat(Stats.TARGET_HIT);
			Criteria.TARGET_HIT.trigger(player, projectile, hit.getPos(), power);
		}
	}

	private static int trigger(WorldAccess world, BlockState state, BlockHitResult hitResult, Entity entity) {
		int power = TargetTaterBlock.calculatePower(hitResult, hitResult.getPos());
		int delay = entity instanceof PersistentProjectileEntity ? RECOVERABLE_POWER_DELAY : REGULAR_POWER_DELAY;
		if (!world.getBlockTickScheduler().isQueued(hitResult.getBlockPos(), state.getBlock())) {
			TargetTaterBlock.setPower(world, state, power, hitResult.getBlockPos(), delay);
		}
		return power;
	}

	private static int calculatePower(BlockHitResult hitResult, Vec3d pos) {
		Direction direction = hitResult.getSide();
		double x = Math.abs(MathHelper.fractionalPart(pos.x) - 0.5);
		double y = Math.abs(MathHelper.fractionalPart(pos.y) - 0.5);
		double z = Math.abs(MathHelper.fractionalPart(pos.z) - 0.5);
		Direction.Axis axis = direction.getAxis();
		double g = axis == Direction.Axis.Y ? Math.max(x, z) : (axis == Direction.Axis.Z ? Math.max(x, y) : Math.max(y, z));
		return Math.max(1, MathHelper.ceil(15.0 * MathHelper.clamp((0.5 - g) / 0.5, 0.0, 1.0)));
	}

	private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
		world.setBlockState(pos, state.with(POWER, power), Block.NOTIFY_ALL);
		world.scheduleBlockTick(pos, state.getBlock(), delay);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(POWER) != 0) {
			world.setBlockState(pos, state.with(POWER, 0), Block.NOTIFY_ALL);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWER);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(POWER);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (world.isClient() || state.isOf(oldState.getBlock())) {
			return;
		}
		if (state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
			world.setBlockState(pos, state.with(POWER, 0), Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
		}
	}
}
