package xyz.nucleoid.extras.mixin.sidebar;

import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimension, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimension, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void extras$addPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.getRegistryKey() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.get().addPlayer(player);
        }
    }

    @Inject(method = "removePlayer", at = @At("RETURN"))
    private void extras$removePlayer(ServerPlayerEntity player, Entity.RemovalReason reason, CallbackInfo ci) {
        if (this.getRegistryKey() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.get().removePlayer(player);
        }
    }
}
