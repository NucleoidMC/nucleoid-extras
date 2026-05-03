package xyz.nucleoid.extras.lobby;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.criterion.TaterCollectedCriterion;
import xyz.nucleoid.extras.lobby.criterion.WearTaterCriterion;

public class NECriteria {
	public static final ResourceLocation TATER_COLLECTED_ID = NucleoidExtras.identifier("tater_collected");
	public static final TaterCollectedCriterion TATER_COLLECTED = new TaterCollectedCriterion();

	public static final ResourceLocation WEAR_TATER_ID = NucleoidExtras.identifier("wear_tater");
	public static final WearTaterCriterion WEAR_TATER = new WearTaterCriterion();

	public static void register() {
		CriteriaTriggers.register(TATER_COLLECTED_ID.toString(), TATER_COLLECTED);
		CriteriaTriggers.register(WEAR_TATER_ID.toString(), WEAR_TATER);
	}
}
