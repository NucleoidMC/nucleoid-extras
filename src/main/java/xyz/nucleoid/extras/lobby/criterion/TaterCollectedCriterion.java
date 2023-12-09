package xyz.nucleoid.extras.lobby.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;

import java.util.Optional;

public class TaterCollectedCriterion extends AbstractCriterion<TaterCollectedCriterion.Conditions> {
	@Override
	protected TaterCollectedCriterion.Conditions conditionsFromJson(JsonObject obj, Optional<LootContextPredicate> playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Identifier tater = obj.has("tater") ? new Identifier(obj.get("tater").getAsString()) : null;
		if(tater != null && !Registries.BLOCK.containsId(tater)) {
			throw new JsonSyntaxException("No tater exists with ID "+tater+"!");
		}
		Integer count = obj.has("count") ? obj.get("count").getAsString().equals("all") ? CubicPotatoBlock.TATERS.size() : obj.get("count").getAsInt() : null;
		return new Conditions(playerPredicate, tater, count);
	}

	public void trigger(ServerPlayerEntity player, Identifier tater, int count) {
		this.trigger(player, conditions -> conditions.matches(tater, count));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier tater;
		private final Integer count;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Identifier tater, Integer count) {
			super(playerPredicate);
			this.tater = tater;
			this.count = count;
		}

		public Identifier getTater() {
			return tater;
		}

		public Integer getCount() {
			return count;
		}

		public boolean matches(Identifier tater, int count) {
			boolean taterMatches = getTater() == null || getTater().equals(tater);
			boolean countMatches = getCount() == null || getCount() <= count;
			return taterMatches && countMatches;
		}
	}
}
