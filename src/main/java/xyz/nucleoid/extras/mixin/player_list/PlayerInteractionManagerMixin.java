package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class PlayerInteractionManagerMixin {
    @Redirect(
            method = "setGameMode(Lnet/minecraft/world/GameMode;Lnet/minecraft/world/GameMode;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"
            )
    )
    private void overrideGameModeListUpdate(PlayerManager playerManager, Packet<?> whitePacket) {
        PlayerListS2CPacket.Entry entry = ((PlayerListS2CPacketAccessor) whitePacket).nucleoid$getEntries().get(0);
        ServerPlayerEntity player = playerManager.getPlayer(entry.getProfile().getId());

        if (player == null) return;

        PlayerListS2CPacket grayPacket = PlayerListHelper.getUpdateGameModeNamePacket(player, true);

        for (ServerPlayerEntity target : playerManager.getPlayerList()) {
            if (PlayerListHelper.shouldGray(player, target)) {
                target.networkHandler.sendPacket(grayPacket);
            } else {
                target.networkHandler.sendPacket(whitePacket);
            }
        }
    }
}
