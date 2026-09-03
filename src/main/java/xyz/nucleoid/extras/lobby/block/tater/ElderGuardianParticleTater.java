package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.particle.ParticleTypes;

public class ElderGuardianParticleTater extends CubicPotatoBlock {
	public ElderGuardianParticleTater(Settings settings, String texture) {
		super(settings, ParticleTypes.ELDER_GUARDIAN, texture, 10000);
	}

	@Override
	public int getBlockParticleChance() {
		return 50;
	}
}
