package xyz.nucleoid.sidebar.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.sidebar.NucleoidSidebar;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    private ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debug, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debug, seed);
    }

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void addPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.getRegistryKey() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.addPlayer(player);
        }
    }

    @Inject(method = "removePlayer", at = @At("RETURN"))
    private void removePlayer(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.getRegistryKey() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.removePlayer(player);
        }
    }
}
