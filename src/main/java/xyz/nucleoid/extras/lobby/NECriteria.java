package xyz.nucleoid.extras.lobby;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.criterion.TaterCollectedCriterion;
import xyz.nucleoid.extras.lobby.criterion.WearTaterCriterion;

public class NECriteria {
	public static final Identifier TATER_COLLECTED_ID = NucleoidExtras.identifier("tater_collected");
	public static final TaterCollectedCriterion TATER_COLLECTED = new TaterCollectedCriterion();

	public static final Identifier WEAR_TATER_ID = NucleoidExtras.identifier("wear_tater");
	public static final WearTaterCriterion WEAR_TATER = new WearTaterCriterion();

	public static void register() {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, TATER_COLLECTED_ID, TATER_COLLECTED);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, WEAR_TATER_ID, WEAR_TATER);
	}
}
