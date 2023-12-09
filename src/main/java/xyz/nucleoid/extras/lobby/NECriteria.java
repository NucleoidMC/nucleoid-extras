package xyz.nucleoid.extras.lobby;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.criterion.TaterCollectedCriterion;
import xyz.nucleoid.extras.lobby.criterion.WearTaterCriterion;

public class NECriteria {
	public static final Identifier TATER_COLLECTED_ID = NucleoidExtras.identifier("tater_collected");
	public static final TaterCollectedCriterion TATER_COLLECTED = new TaterCollectedCriterion();

	public static final Identifier WEAR_TATER_ID = NucleoidExtras.identifier("wear_tater");
	public static final WearTaterCriterion WEAR_TATER = new WearTaterCriterion();

	public static void register() {
		Criteria.register(TATER_COLLECTED_ID.toString(), TATER_COLLECTED);
		Criteria.register(WEAR_TATER_ID.toString(), WEAR_TATER);
	}
}
