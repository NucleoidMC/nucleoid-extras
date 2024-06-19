package xyz.nucleoid.extras.lobby.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class WearTaterCriterion extends AbstractCriterion<WearTaterCriterion.Conditions> {
	public static final Calendar CALENDAR = Calendar.getInstance();

	public void trigger(ServerPlayerEntity player, TinyPotatoBlock tater) {
		CALENDAR.setTime(new Date());
		this.trigger(player, conditions -> conditions.matches(tater, CALENDAR.get(Calendar.DAY_OF_WEEK)));
	}

	public static int dayOfWeekToInt(String day) {
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

    public static String dayOfWeekToString(int day) {
        return switch (day) {
            case Calendar.MONDAY -> "monday";
            case Calendar.TUESDAY -> "tuesday";
            case Calendar.WEDNESDAY -> "wednesday";
            case Calendar.THURSDAY -> "thursday";
            case Calendar.FRIDAY -> "friday";
            case Calendar.SATURDAY -> "saturday";
            case Calendar.SUNDAY -> "sunday";
            default -> Integer.toString(day);
        };
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<RegistryEntry<Block>> tater, Optional<Integer> dayOfWeek) implements AbstractCriterion.Conditions {
        private static final Codec<Integer> DAY_OF_WEEK_CODEC = Codec.STRING.xmap(WearTaterCriterion::dayOfWeekToInt, WearTaterCriterion::dayOfWeekToString);

        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.createStrictOptionalFieldCodec(TinyPotatoBlock.ENTRY_CODEC, "tater").forGetter(Conditions::tater),
            Codecs.createStrictOptionalFieldCodec(DAY_OF_WEEK_CODEC, "day_of_week").forGetter(Conditions::dayOfWeek)
        ).apply(instance, Conditions::new));

        public boolean matches(TinyPotatoBlock tater, int dayOfWeek) {
            boolean taterMatches = this.tater.isEmpty() || this.tater.get().value() == tater;
            boolean dayOfWeekMatches = this.dayOfWeek.isEmpty() || this.dayOfWeek.get() == dayOfWeek;
            return taterMatches && dayOfWeekMatches;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }
}
