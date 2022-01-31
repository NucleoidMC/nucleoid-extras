package xyz.nucleoid.extras.lobby.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.block.TinyPotatoBlock;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TaterCollectedCriterion extends AbstractCriterion<TaterCollectedCriterion.Conditions> {
	public static final Identifier ID = NucleoidExtras.identifier("tater_collected");

	@Override
	protected TaterCollectedCriterion.Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Identifier tater = obj.has("tater") ? new Identifier(obj.get("tater").getAsString()) : null;
		if(tater != null && !Registry.BLOCK.containsId(tater)) {
			throw new JsonSyntaxException("No tater exists with ID "+tater+"!");
		}
		Integer count = obj.has("count") ? obj.get("count").getAsString().equals("all") ? TinyPotatoBlock.TATERS.size() : obj.get("count").getAsInt() : null;
		return new Conditions(playerPredicate, tater, count);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public void trigger(ServerPlayerEntity player, Identifier tater, int count) {
		this.trigger(player, conditions -> conditions.matches(tater, count));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier tater;
		private final Integer count;

		public Conditions(EntityPredicate.Extended playerPredicate, Identifier tater, Integer count) {
			super(ID, playerPredicate);
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
