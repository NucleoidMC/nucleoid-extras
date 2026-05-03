package xyz.nucleoid.extras.mixin.player_list;

import eu.pb4.polymer.core.mixin.entity.PlayerListS2CPacketAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.player_list.PlayerListHelper;

import java.util.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin {
    @Shadow @Final private Map<UUID, ServerPlayer> playersByUUID;

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void extras$sendToOthersOnJoin(PlayerList playerManager, Packet<?> whitePacket) {
        var entry = ((ClientboundPlayerInfoUpdatePacket) whitePacket).entries().get(0);
        var player = playerManager.getPlayer(entry.profileId());

        var grayPacket = PlayerListHelper.createAddPacket(player, true);

        for (var target : this.playersByUUID.values()) {
            if (PlayerListHelper.shouldGray(player, target)) {
                target.connection.send(grayPacket);
            } else {
                target.connection.send(whitePacket);
            }
        }
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;createPlayerInitializing(Ljava/util/Collection;)Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket;", ordinal = 0))
    private ClientboundPlayerInfoUpdatePacket extras$sendOthersToJoining(Collection<ServerPlayer> players, Connection connection, ServerPlayer target) {
        var packet = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of());

        var entries = new ArrayList<ClientboundPlayerInfoUpdatePacket.Entry>();

        for (var player : players) {
            entries.add(PlayerListHelper.createEntry(player, PlayerListHelper.shouldGray(player, target)));
        }

        ((PlayerListS2CPacketAccessor) packet).setEntries(entries);

        return packet;
    }

}
