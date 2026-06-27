package xyz.nucleoid.extras.mixin.sidebar;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {
    protected ServerWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimension, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimension, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void extras$addPlayer(ServerPlayer player, CallbackInfo ci) {
        if (this.dimension() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.get().addPlayer(player);
        }
    }

    @Inject(method = "removePlayerImmediately", at = @At("RETURN"))
    private void extras$removePlayer(ServerPlayer player, Entity.RemovalReason reason, CallbackInfo ci) {
        if (this.dimension() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.get().removePlayer(player);
        }
    }
}
