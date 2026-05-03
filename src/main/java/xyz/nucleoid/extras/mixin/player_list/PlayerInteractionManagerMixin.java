package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

@Mixin(ServerPlayerGameMode.class)
public abstract class PlayerInteractionManagerMixin {
    @Redirect(
            method = "changeGameModeForPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void extras$overrideGameModeListUpdate(PlayerList playerManager, Packet<?> whitePacket) {
        var entry = ((ClientboundPlayerInfoUpdatePacket) whitePacket).entries().get(0);
        var player = playerManager.getPlayer(entry.profileId());

        if (player == null) return;

        for (var target : playerManager.getPlayers()) {
            if (!PlayerListHelper.shouldGray(player, target)) {
                target.connection.send(whitePacket);
            }
        }
    }
}
