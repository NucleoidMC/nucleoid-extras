package xyz.nucleoid.extras.mixin;

import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MetricsData.class)
public interface MetricsDataAccessor {
    @Accessor
    long[] nucleoid$getSamples();
}
