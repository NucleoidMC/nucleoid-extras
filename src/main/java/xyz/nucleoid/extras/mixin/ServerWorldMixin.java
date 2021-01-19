package xyz.nucleoid.extras.mixin;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
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
    @Shadow @Final public MinecraftServer server;

    @Inject(method = "addPlayer", at = @At("TAIL"))
    private void nucleoid$playerRefreshTablist(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerListS2CPacket packetNormal = PlayerListHelper.getUpdateDisplayNamePacket(player, false);
        PlayerListS2CPacket packetGray = PlayerListHelper.getUpdateDisplayNamePacket(player, true);

        PlayerListS2CPacket packetUpdatePlayer = new PlayerListS2CPacket();
        ((PlayerListS2CPacketAccessor) packetUpdatePlayer).nucleoid$setAction(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);

        for (ServerPlayerEntity playerOther : this.server.getPlayerManager().getPlayerList()) {
            playerOther.networkHandler.sendPacket(player.world == playerOther.world ? packetNormal : packetGray);
            ((PlayerListS2CPacketAccessor) packetUpdatePlayer).nucleoid$getEntries().add(
                    packetUpdatePlayer.new Entry(playerOther.getGameProfile(), 0, playerOther.interactionManager.getGameMode(),
                            PlayerListHelper.getDisplayName(playerOther, player.world != playerOther.world))
            );
        }

        player.networkHandler.sendPacket(packetUpdatePlayer);
    }
}
