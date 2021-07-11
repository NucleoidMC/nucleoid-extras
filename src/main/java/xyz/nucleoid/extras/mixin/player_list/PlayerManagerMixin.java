package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow @Nullable public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    private void sendToOthersOnJoin(PlayerManager playerManager, Packet<?> whitePacket) {
        var entry = ((PlayerListS2CPacket) whitePacket).getEntries().get(0);
        var player = playerManager.getPlayer(entry.getProfile().getId());

        var grayPacket = PlayerListHelper.getAddPlayerPacket(player, true);

        for (var target : this.playerMap.values()) {
            if (PlayerListHelper.shouldGray(player, target)) {
                target.networkHandler.sendPacket(grayPacket);
            } else {
                target.networkHandler.sendPacket(whitePacket);
            }
        }
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 7))
    private void sendOthersToJoining(ServerPlayNetworkHandler networkHandler, Packet<?> whitePacket) {
        var entry = ((PlayerListS2CPacket) whitePacket).getEntries().get(0);
        var player = this.getPlayer(entry.getProfile().getId());

        if (PlayerListHelper.shouldGray(player, networkHandler.player)) {
            networkHandler.sendPacket(PlayerListHelper.getAddPlayerPacket(player, true));
        } else {
            networkHandler.sendPacket(whitePacket);
        }
    }

}
