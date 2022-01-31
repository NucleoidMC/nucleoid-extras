package xyz.nucleoid.extras.lobby.criterion;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import xyz.nucleoid.extras.NucleoidExtras;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class WearTaterCriterion extends AbstractCriterion<WearTaterCriterion.Conditions> {
	public static final Identifier ID = NucleoidExtras.identifier("wear_tater");

	public static final Calendar CALENDAR = Calendar.getInstance();

	@Override
	protected WearTaterCriterion.Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Identifier tater = obj.has("tater") ? new Identifier(obj.get("tater").getAsString()) : null;
		if(tater != null && !Registry.BLOCK.containsId(tater)) {
			throw new JsonSyntaxException("No tater exists with ID "+tater+"!");
		}
		Integer dayOfWeek = obj.has("day_of_week") ? dayOfWeekToInt(obj.get("day_of_week").getAsString()) : null;
		if(dayOfWeek != null && (dayOfWeek > 7 || dayOfWeek < 1)) {
			throw new JsonSyntaxException("Invalid day of week specified!");
		}
		return new Conditions(playerPredicate, tater, dayOfWeek);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public void trigger(ServerPlayerEntity player, Identifier tater) {
		CALENDAR.setTime(new Date());
		this.trigger(player, conditions -> conditions.matches(tater, CALENDAR.get(Calendar.DAY_OF_WEEK)));
	}

	public int dayOfWeekToInt(String day) {
		return switch (day.toLowerCase(Locale.ROOT)) {
			case "monday" -> Calendar.MONDAY;
			case "tuesday" -> Calendar.TUESDAY;
			case "wednesday" -> Calendar.WEDNESDAY;
			case "thursday" -> Calendar.THURSDAY;
			case "friday" -> Calendar.FRIDAY;
			case "saturday" -> Calendar.SATURDAY;
			case "sunday" -> Calendar.SUNDAY;
			default -> Integer.parseInt(day);
		};
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier tater;
		private final Integer dayOfWeek;

		public Conditions(EntityPredicate.Extended playerPredicate, Identifier tater, Integer dayOfWeek) {
			super(ID, playerPredicate);
			this.tater = tater;
			this.dayOfWeek = dayOfWeek;
		}

		public Identifier getTater() {
			return tater;
		}

		public Integer getDayOfWeek() {
			return dayOfWeek;
		}

		public boolean matches(Identifier tater, int dayOfWeek) {
			boolean taterMatches = getTater() == null || getTater().equals(tater);
			boolean dayOfWeekMatches = getDayOfWeek() == null || getDayOfWeek() == dayOfWeek;
			return taterMatches && dayOfWeekMatches;
		}
	}
}
