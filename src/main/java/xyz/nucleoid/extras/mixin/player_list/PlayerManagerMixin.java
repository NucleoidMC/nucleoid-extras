package xyz.nucleoid.extras.mixin.player_list;

import eu.pb4.polymer.core.mixin.entity.PlayerListS2CPacketAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

import java.util.*;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    private void extras$sendToOthersOnJoin(PlayerManager playerManager, Packet<?> whitePacket) {
        var entry = ((PlayerListS2CPacket) whitePacket).getEntries().get(0);
        var player = playerManager.getPlayer(entry.profileId());

        var grayPacket = PlayerListHelper.createAddPacket(player, true);

        for (var target : this.playerMap.values()) {
            if (PlayerListHelper.shouldGray(player, target)) {
                target.networkHandler.sendPacket(grayPacket);
            } else {
                target.networkHandler.sendPacket(whitePacket);
            }
        }
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;entryFromPlayer(Ljava/util/Collection;)Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;", ordinal = 0))
    private PlayerListS2CPacket extras$sendOthersToJoining(Collection<ServerPlayerEntity> players, ClientConnection connection, ServerPlayerEntity target) {
        var packet = PlayerListS2CPacket.entryFromPlayer(List.of());

        var entries = new ArrayList<PlayerListS2CPacket.Entry>();

        for (var player : players) {
            entries.add(PlayerListHelper.createEntry(player, PlayerListHelper.shouldGray(player, target)));
        }

        ((PlayerListS2CPacketAccessor) packet).setEntries(entries);

        return packet;
    }

}
