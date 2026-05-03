package xyz.nucleoid.extras.lobby.block.tater;

import xyz.nucleoid.extras.lobby.NEBlocks;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BellTaterBlockEntity extends BlockEntity {
	private long lastRingTime;
	public int ringTicks;
	public boolean ringing;
	private List<LivingEntity> hearingEntities = List.of();
	private boolean resonating;
	private int resonateTime;

	public BellTaterBlockEntity(BlockPos pos, BlockState state) {
		super(NEBlocks.BELL_TATER_ENTITY, pos, state);
	}

	@Override
	public boolean triggerEvent(int type, int data) {
		if (type == 1) {
			this.notifyMemoriesOfBell();
			this.ringTicks = 0;
			this.ringing = true;
			return true;
		}
		return super.triggerEvent(type, data);
	}

	private static void tick(Level world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity, BellBlockEntity.ResonationEndAction bellEffect) {
		if (blockEntity.ringing) {
			++blockEntity.ringTicks;
		}
		if (blockEntity.ringTicks >= 50) {
			blockEntity.ringing = false;
			blockEntity.ringTicks = 0;
		}
		if (blockEntity.ringTicks >= 5 && blockEntity.resonateTime == 0 && BellBlockEntity.areRaidersNearby(pos, blockEntity.hearingEntities)) {
			blockEntity.resonating = true;
			world.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		if (blockEntity.resonating) {
			if (blockEntity.resonateTime < 40) {
				++blockEntity.resonateTime;
			} else {
				bellEffect.run(world, pos, blockEntity.hearingEntities);
				blockEntity.resonating = false;
			}
		}
	}

	public static void clientTick(Level world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity) {
		BellTaterBlockEntity.tick(world, pos, state, blockEntity, BellBlockEntity::showBellParticles);
	}

	public static void serverTick(Level world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity) {
		BellTaterBlockEntity.tick(world, pos, state, blockEntity, BellBlockEntity::makeRaidersGlow);
	}

	/**
	 * Rings the bell in a given direction.
	 */
	public void activate(Direction direction) {
		BlockPos blockPos = this.getBlockPos();
		if (this.ringing) {
			this.ringTicks = 0;
		} else {
			this.ringing = true;
		}
		this.level.blockEvent(blockPos, this.getBlockState().getBlock(), 1, direction.get3DDataValue());
	}

	/**
	 * Makes living entities within 48 blocks remember that they heard a bell at the current world time.
	 */
	private void notifyMemoriesOfBell() {
		BlockPos blockPos = this.getBlockPos();
		if (this.level.getGameTime() > this.lastRingTime + 60L || this.hearingEntities == null) {
			this.lastRingTime = this.level.getGameTime();
			AABB box = new AABB(blockPos).inflate(48.0);
			this.hearingEntities = this.level.getEntitiesOfClass(LivingEntity.class, box);
		}
		if (!this.level.isClientSide()) {
			for (LivingEntity livingEntity : this.hearingEntities) {
				if (!livingEntity.isAlive() || livingEntity.isRemoved() || !blockPos.closerToCenterThan(livingEntity.position(), 32.0)) continue;
				livingEntity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
			}
		}
	}
}
