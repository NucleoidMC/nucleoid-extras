package xyz.nucleoid.extras.lobby.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;

import java.util.Optional;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

public class TaterCollectedCriterion extends SimpleCriterionTrigger<TaterCollectedCriterion.Conditions> {
	public void trigger(ServerPlayer player, TinyPotatoBlock tater, int count) {
		this.trigger(player, conditions -> conditions.matches(player, tater, count));
	}

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class Conditions implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TinyPotatoBlock.ENTRY_CODEC.optionalFieldOf("tater").forGetter(i -> i.tater),
            TaterCount.CODEC.optionalFieldOf("count").forGetter(i -> i.count)
        ).apply(instance, Conditions::new));

        private final Optional<Holder<Block>> tater;
        private final Optional<TaterCount> count;

        public Conditions(Optional<Holder<Block>> tater, Optional<TaterCount> count) {
            this.tater = tater;
            this.count = count;
        }

        public boolean matches(ServerPlayer player, TinyPotatoBlock tater, int count) {
            boolean taterMatches = this.tater.isEmpty() || this.tater.get().value() == tater;
            boolean countMatches = this.count.isEmpty() || this.count.get().matches(player.registryAccess(), count);
            return taterMatches && countMatches;
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }
}
