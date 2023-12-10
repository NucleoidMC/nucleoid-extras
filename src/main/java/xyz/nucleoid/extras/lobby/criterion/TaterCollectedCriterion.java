package xyz.nucleoid.extras.lobby.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;

import java.util.Optional;

public class TaterCollectedCriterion extends AbstractCriterion<TaterCollectedCriterion.Conditions> {
	public void trigger(ServerPlayerEntity player, Identifier tater, int count) {
		this.trigger(player, conditions -> conditions.matches(tater, count));
	}

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class Conditions implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("tater").forGetter(Conditions::getTater),
            Codec.INT.optionalFieldOf("count").forGetter(i -> i.optionalCount)
        ).apply(instance, Conditions::new));

        private final Identifier tater;
		private final Integer count;
        private final Optional<Integer> optionalCount;

        public Conditions(Identifier tater, Optional<Integer> count) {
			this.tater = tater;
			this.count = count.orElse(TinyPotatoBlock.TATERS.size());
            this.optionalCount = count;
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

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }
}
