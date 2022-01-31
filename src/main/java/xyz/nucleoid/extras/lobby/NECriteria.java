package xyz.nucleoid.extras.lobby;

import xyz.nucleoid.extras.lobby.criterion.TaterCollectedCriterion;
import xyz.nucleoid.extras.lobby.criterion.WearTaterCriterion;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class NECriteria {
	public static final TaterCollectedCriterion TATER_COLLECTED = new TaterCollectedCriterion();
	public static final WearTaterCriterion WEAR_TATER = new WearTaterCriterion();

	public static void register() {
		CriterionRegistry.register(TATER_COLLECTED);
		CriterionRegistry.register(WEAR_TATER);
	}
}
