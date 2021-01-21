package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.PlayerListHelper;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Inject(method = "addPlayer", at = @At("TAIL"))
    private void onPlayerJoinWorld(ServerPlayerEntity joinedPlayer, CallbackInfo ci) {
        PlayerListHelper.updatePlayer(joinedPlayer);
    }
}
