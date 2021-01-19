package xyz.nucleoid.extras.mixin;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.extras.PlayerListHelper;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow @Nullable public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    private void nucleoid$sendToAll(PlayerManager playerManager, Packet<?> packet1) {
        PlayerListS2CPacket whitePacket = (PlayerListS2CPacket) packet1;
        PlayerListS2CPacket.Entry entry = ((PlayerListS2CPacketAccessor) whitePacket).nucleoid$getEntries().get(0);

        PlayerListS2CPacket grayPacket = new PlayerListS2CPacket();
        ((PlayerListS2CPacketAccessor) grayPacket).nucleoid$setAction(PlayerListS2CPacket.Action.ADD_PLAYER);

        Text grayedText = PlayerListHelper.getDisplayName(this.playerMap.get(entry.getProfile().getId()), true);
        PlayerListS2CPacket.Entry grayedEntry = grayPacket.new Entry(entry.getProfile(), entry.getLatency(), entry.getGameMode(), grayedText);
        ((PlayerListS2CPacketAccessor) grayPacket).nucleoid$getEntries().add(grayedEntry);

        ServerPlayerEntity player = playerManager.getPlayer(entry.getProfile().getId());

        for (ServerPlayerEntity target : this.playerMap.values()) {
            if (player.world == target.world) {
                target.networkHandler.sendPacket(whitePacket);
            } else {
                target.networkHandler.sendPacket(grayPacket);
            }
        }
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 7))
    private void nucleoid$sendPlayersToJoining(ServerPlayNetworkHandler serverPlayNetworkHandler, Packet<?> packet1) {
        PlayerListS2CPacket whitePacket = (PlayerListS2CPacket) packet1;
        PlayerListS2CPacket.Entry entry = ((PlayerListS2CPacketAccessor) whitePacket).nucleoid$getEntries().get(0);

        ServerPlayerEntity player = this.getPlayer(entry.getProfile().getId());

        if (serverPlayNetworkHandler.player.world == player.world) {
            serverPlayNetworkHandler.sendPacket(whitePacket);
        } else {
            PlayerListS2CPacket packet = new PlayerListS2CPacket();
            ((PlayerListS2CPacketAccessor) packet).nucleoid$setAction(PlayerListS2CPacket.Action.ADD_PLAYER);


            PlayerListS2CPacket.Entry grayedEntry = packet.new Entry(entry.getProfile(), entry.getLatency(), entry.getGameMode(),
                    PlayerListHelper.getDisplayName(player, true)
            );

            ((PlayerListS2CPacketAccessor) packet).nucleoid$getEntries().add(grayedEntry);

            serverPlayNetworkHandler.sendPacket(packet);
        }
    }

}
