package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.util.math.BlockPos;

public sealed interface LuckyTaterDropPos {
    public record Allowed(BlockPos pos) implements LuckyTaterDropPos {}

    public record Blocked(BlockPos pos) implements LuckyTaterDropPos {}

    public final class None implements LuckyTaterDropPos {
        public static final None INSTANCE = new None();

        private None() {
            return;
        }
    }
}
