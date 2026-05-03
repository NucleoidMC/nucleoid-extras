package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "addPlayerToTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerScoreboard;setDirty()V", shift = At.Shift.AFTER))
    private void extras$updatePlayerAfterJoining(String playerName, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
        var player = this.server.getPlayerList().getPlayerByName(playerName);
        if (player != null) {
            PlayerListHelper.updatePlayer(player);
        }
    }

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)V", at = @At("TAIL"))
    private void extras$updatePlayerAfterLeaving(String playerName, PlayerTeam team, CallbackInfo ci) {
        var player = this.server.getPlayerList().getPlayerByName(playerName);
        if (player != null) {
            PlayerListHelper.updatePlayer(player);
        }
    }

    @Inject(method = "onTeamRemoved", at = @At("TAIL"))
    private void extras$updatePlayerAfterRemovingTeam(PlayerTeam team, CallbackInfo ci) {
        for (var playerName : team.getPlayers()) {
            var player = this.server.getPlayerList().getPlayerByName(playerName);
            if (player != null) {
                PlayerListHelper.updatePlayer(player);
            }
        }
    }
}
