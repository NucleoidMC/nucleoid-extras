package xyz.nucleoid.extras.lobby.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;

import java.util.Optional;

public class TaterCollectedCriterion extends AbstractCriterion<TaterCollectedCriterion.Conditions> {
	public void trigger(ServerPlayerEntity player, TinyPotatoBlock tater, int count) {
		this.trigger(player, conditions -> conditions.matches(tater, count));
	}

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class Conditions implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TinyPotatoBlock.ENTRY_CODEC.optionalFieldOf("tater").forGetter(i -> i.tater),
            TaterCount.CODEC.optionalFieldOf("count").forGetter(i -> i.count)
        ).apply(instance, Conditions::new));

        private final Optional<RegistryEntry<Block>> tater;
        private final Optional<TaterCount> count;

        public Conditions(Optional<RegistryEntry<Block>> tater, Optional<TaterCount> count) {
            this.tater = tater;
            this.count = count;
        }

        public boolean matches(TinyPotatoBlock tater, int count) {
            boolean taterMatches = this.tater.isEmpty() || this.tater.get().value() == tater;
            boolean countMatches = this.count.isEmpty() || this.count.get().matches(count);
            return taterMatches && countMatches;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return Optional.empty();
        }
    }
}
