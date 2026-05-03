package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.particles.ParticleTypes;

public class ElderGuardianParticleTater extends CubicPotatoBlock {
	public ElderGuardianParticleTater(Properties settings, String texture) {
		super(settings, ParticleTypes.ELDER_GUARDIAN, texture, 10000);
	}

	@Override
	public int getBlockParticleChance() {
		return 50;
	}
}
