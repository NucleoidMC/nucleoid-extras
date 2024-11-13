package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;

import java.util.List;

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
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.notifyMemoriesOfBell();
			this.ringTicks = 0;
			this.ringing = true;
			return true;
		}
		return super.onSyncedBlockEvent(type, data);
	}

	private static void tick(World world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity, BellBlockEntity.Effect bellEffect) {
		if (blockEntity.ringing) {
			++blockEntity.ringTicks;
		}
		if (blockEntity.ringTicks >= 50) {
			blockEntity.ringing = false;
			blockEntity.ringTicks = 0;
		}
		if (blockEntity.ringTicks >= 5 && blockEntity.resonateTime == 0 && BellBlockEntity.raidersHearBell(pos, blockEntity.hearingEntities)) {
			blockEntity.resonating = true;
			world.playSound(null, pos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
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

	public static void clientTick(World world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity) {
		BellTaterBlockEntity.tick(world, pos, state, blockEntity, BellBlockEntity::applyParticlesToRaiders);
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity) {
		BellTaterBlockEntity.tick(world, pos, state, blockEntity, BellBlockEntity::applyGlowToRaiders);
	}

	/**
	 * Rings the bell in a given direction.
	 */
	public void activate(Direction direction) {
		BlockPos blockPos = this.getPos();
		if (this.ringing) {
			this.ringTicks = 0;
		} else {
			this.ringing = true;
		}
		this.world.addSyncedBlockEvent(blockPos, this.getCachedState().getBlock(), 1, direction.getId());
	}

	/**
	 * Makes living entities within 48 blocks remember that they heard a bell at the current world time.
	 */
	private void notifyMemoriesOfBell() {
		BlockPos blockPos = this.getPos();
		if (this.world.getTime() > this.lastRingTime + 60L || this.hearingEntities == null) {
			this.lastRingTime = this.world.getTime();
			Box box = new Box(blockPos).expand(48.0);
			this.hearingEntities = this.world.getNonSpectatingEntities(LivingEntity.class, box);
		}
		if (!this.world.isClient) {
			for (LivingEntity livingEntity : this.hearingEntities) {
				if (!livingEntity.isAlive() || livingEntity.isRemoved() || !blockPos.isWithinDistance(livingEntity.getPos(), 32.0)) continue;
				livingEntity.getBrain().remember(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
			}
		}
	}
}
