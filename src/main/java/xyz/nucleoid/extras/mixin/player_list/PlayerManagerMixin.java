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
import xyz.nucleoid.extras.PlayerListHelper;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow @Nullable public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    private void nucleoid$sendToAll(PlayerManager playerManager, Packet<?> whitePacket) {
        PlayerListS2CPacket.Entry entry = ((PlayerListS2CPacketAccessor) whitePacket).nucleoid$getEntries().get(0);
        ServerPlayerEntity player = playerManager.getPlayer(entry.getProfile().getId());

        PlayerListS2CPacket grayPacket = PlayerListHelper.getAddPlayerPacket(player, true);

        for (ServerPlayerEntity target : this.playerMap.values()) {

            if (ManagedGameSpace.forWorld(player.world) == ManagedGameSpace.forWorld(target.world)) {
                target.networkHandler.sendPacket(whitePacket);
            } else {
                target.networkHandler.sendPacket(grayPacket);
            }
        }
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 7))
    private void nucleoid$sendPlayersToJoining(ServerPlayNetworkHandler serverPlayNetworkHandler, Packet<?> whitePacket) {
        PlayerListS2CPacket.Entry entry = ((PlayerListS2CPacketAccessor) whitePacket).nucleoid$getEntries().get(0);
        ServerPlayerEntity player = this.getPlayer(entry.getProfile().getId());

        if (ManagedGameSpace.forWorld(serverPlayNetworkHandler.player.world) == ManagedGameSpace.forWorld(player.world)) {
            serverPlayNetworkHandler.sendPacket(whitePacket);
        } else {
            PlayerListS2CPacket packet = PlayerListHelper.getAddPlayerPacket(player, true);
            serverPlayNetworkHandler.sendPacket(packet);
        }
    }

}
