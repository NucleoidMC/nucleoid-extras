package xyz.nucleoid.extras.lobby;

import xyz.nucleoid.extras.lobby.criterion.TaterCollectedCriterion;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class NECriteria {
	public static final TaterCollectedCriterion TATER_COLLECTED = new TaterCollectedCriterion();

	public static void register() {
		CriterionRegistry.register(TATER_COLLECTED);
	}
}
