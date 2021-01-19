package xyz.nucleoid.extras.mixin.player_list;

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
    @Shadow
    @Final
    public MinecraftServer server;

    @Inject(method = "addPlayer", at = @At("TAIL"))
    private void onPlayerJoinWorld(ServerPlayerEntity joinedPlayer, CallbackInfo ci) {
        PlayerListS2CPacket normalPacket = PlayerListHelper.getUpdateDisplayNamePacket(joinedPlayer, false);
        PlayerListS2CPacket grayPacket = PlayerListHelper.getUpdateDisplayNamePacket(joinedPlayer, true);

        PlayerListS2CPacket updateJoined = new PlayerListS2CPacket();
        ((PlayerListS2CPacketAccessor) updateJoined).nucleoid$setAction(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);

        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            boolean gray = PlayerListHelper.shouldGray(player, joinedPlayer);

            player.networkHandler.sendPacket(gray ? grayPacket : normalPacket);
            PlayerListS2CPacket.Entry entry = updateJoined.new Entry(
                    player.getGameProfile(),
                    0,
                    player.interactionManager.getGameMode(),
                    PlayerListHelper.getDisplayName(player, gray)
            );
            ((PlayerListS2CPacketAccessor) updateJoined).nucleoid$getEntries().add(entry);
        }

        joinedPlayer.networkHandler.sendPacket(updateJoined);
    }
}
