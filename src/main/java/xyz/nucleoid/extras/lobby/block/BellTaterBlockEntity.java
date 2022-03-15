package xyz.nucleoid.extras.lobby.block;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import xyz.nucleoid.extras.lobby.NEBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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

	private static void tick(World world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity, BellTaterBlockEntity.Effect bellEffect) {
		if (blockEntity.ringing) {
			++blockEntity.ringTicks;
		}
		if (blockEntity.ringTicks >= 50) {
			blockEntity.ringing = false;
			blockEntity.ringTicks = 0;
		}
		if (blockEntity.ringTicks >= 5 && blockEntity.resonateTime == 0 && BellTaterBlockEntity.raidersHearBell(pos, blockEntity.hearingEntities)) {
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
		BellTaterBlockEntity.tick(world, pos, state, blockEntity, BellTaterBlockEntity::applyParticlesToRaiders);
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, BellTaterBlockEntity blockEntity) {
		BellTaterBlockEntity.tick(world, pos, state, blockEntity, BellTaterBlockEntity::applyGlowToRaiders);
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

	private static boolean raidersHearBell(BlockPos pos, List<LivingEntity> hearingEntities) {
		for (LivingEntity livingEntity : hearingEntities) {
			if (!livingEntity.isAlive() || livingEntity.isRemoved() || !pos.isWithinDistance(livingEntity.getPos(), 32.0) || !livingEntity.getType().isIn(EntityTypeTags.RAIDERS)) continue;
			return true;
		}
		return false;
	}

	private static void applyGlowToRaiders(World world, BlockPos pos, List<LivingEntity> hearingEntities) {
		hearingEntities.stream().filter(entity -> BellTaterBlockEntity.isRaiderEntity(pos, entity)).forEach(BellTaterBlockEntity::applyGlowToEntity);
	}

	/**
	 * Spawns {@link net.minecraft.particle.ParticleTypes#ENTITY_EFFECT} particles around raiders within 48 blocks.
	 */
	private static void applyParticlesToRaiders(World world, BlockPos pos, List<LivingEntity> hearingEntities) {
		MutableInt mutableInt = new MutableInt(16700985);
		int i = (int)hearingEntities.stream().filter(entity -> pos.isWithinDistance(entity.getPos(), 48.0)).count();
		hearingEntities.stream().filter(entity -> BellTaterBlockEntity.isRaiderEntity(pos, entity)).forEach(entity -> {
			float f = 1.0f;
			double d = Math.sqrt((entity.getX() - (double)pos.getX()) * (entity.getX() - (double)pos.getX()) + (entity.getZ() - (double)pos.getZ()) * (entity.getZ() - (double)pos.getZ()));
			double e = (double)((float)pos.getX() + 0.5f) + 1.0 / d * (entity.getX() - (double)pos.getX());
			double g = (double)((float)pos.getZ() + 0.5f) + 1.0 / d * (entity.getZ() - (double)pos.getZ());
			int j = MathHelper.clamp((i - 21) / -2, 3, 15);
			for (int k = 0; k < j; ++k) {
				int l = mutableInt.addAndGet(5);
				double h = (double)(l >> 16 & 0xFF) / 255.0;
				double m = (double)(l >> 8 & 0xFF) / 255.0;
				double n = (double)(l & 0xFF) / 255.0;
				world.addParticle(ParticleTypes.ENTITY_EFFECT, e, (float)pos.getY() + 0.5f, g, h, m, n);
			}
		});
	}

	/**
	 * Determines whether the given entity is in the {@link net.minecraft.tag.EntityTypeTags#RAIDERS} entity type tag and within 48 blocks of the given position.
	 */
	private static boolean isRaiderEntity(BlockPos pos, LivingEntity entity) {
		return entity.isAlive() && !entity.isRemoved() && pos.isWithinDistance(entity.getPos(), 48.0) && entity.getType().isIn(EntityTypeTags.RAIDERS);
	}

	/**
	 * Gives the {@link net.minecraft.entity.effect.StatusEffects#GLOWING} status effect to the given entity for 3 seconds (60 ticks).
	 */
	private static void applyGlowToEntity(LivingEntity entity) {
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));
	}

	@FunctionalInterface
	interface Effect {
		void run(World world, BlockPos pos, List<LivingEntity> hearingEntities);
	}
}
