package xyz.nucleoid.extras.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MetricsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor("metricsData")
    MetricsData nucleoid$getMetricsData();
}
