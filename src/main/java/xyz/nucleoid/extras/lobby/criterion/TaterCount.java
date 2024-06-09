package xyz.nucleoid.extras.lobby.criterion;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.dynamic.Codecs;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;

import java.util.function.Function;

public sealed interface TaterCount {
    static final Codec<TaterCount> CODEC = Codecs.either(Value.CODEC, All.CODEC).xmap(either -> {
        return either.map(Function.identity(), Function.identity());
    }, count -> {
        return count instanceof Value ? Either.left((Value) count) : Either.right((All) count);
    });

    int count();

    default boolean matches(int count) {
        return this.count() <= count;
    }

    record Value(int count) implements TaterCount {
        private static final Codec<Value> CODEC = Codecs.NONNEGATIVE_INT.xmap(Value::new, Value::count);

        public Value {
            if (count < 0) {
                throw new IllegalArgumentException("Count must be non-negative: " + count);
            }
        }
    }

    record All() implements TaterCount {
        private static final String STRING = "all";
        private static final All INSTANCE = new All();

        private static final Codec<All> CODEC = Codec.STRING.comapFlatMap(string -> {
            if (STRING.equals(string)) {
                return DataResult.success(All.INSTANCE);
            }

            return DataResult.error(() -> "Not an 'all' count");
        }, string -> STRING);

        @Override
        public int count() {
            return TinyPotatoBlock.TATERS.size();
        }
    }
}
