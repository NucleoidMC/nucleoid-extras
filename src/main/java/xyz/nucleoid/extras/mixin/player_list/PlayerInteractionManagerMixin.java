package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin {
    @Redirect(
            method = "changeGameMode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void extras$overrideGameModeListUpdate(PlayerManager playerManager, Packet<?> whitePacket) {
        var entry = ((PlayerListS2CPacket) whitePacket).getEntries().get(0);
        var player = playerManager.getPlayer(entry.profileId());

        if (player == null) return;

        for (var target : playerManager.getPlayerList()) {
            if (!PlayerListHelper.shouldGray(player, target)) {
                target.networkHandler.sendPacket(whitePacket);
            }
        }
    }
}
