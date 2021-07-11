package xyz.nucleoid.extras.player_list;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import xyz.nucleoid.extras.mixin.player_list.PlayerListS2CPacketAccessor;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

public class PlayerListHelper {
    public static Text getDisplayName(ServerPlayerEntity player, boolean gray) {
        Text displayName = player.getDisplayName();
        return gray ? displayName.shallowCopy().formatted(Formatting.DARK_GRAY, Formatting.ITALIC) : displayName;
    }

    public static GameMode getGameMode(ServerPlayerEntity player, boolean gray) {
        return gray ? GameMode.SPECTATOR : player.interactionManager.getGameMode();
    }

    public static PlayerListS2CPacket getUpdateGameModeNamePacket(ServerPlayerEntity player, boolean gray) {
        PlayerListS2CPacket packet = new PlayerListS2CPacket();

        PlayerListS2CPacket.Entry entry = packet.new Entry(player.getGameProfile(), 0, getGameMode(player, gray), player.getDisplayName());
        ((PlayerListS2CPacketAccessor) packet).nucleoid$setAction(PlayerListS2CPacket.Action.UPDATE_GAME_MODE);
        ((PlayerListS2CPacketAccessor) packet).nucleoid$getEntries().add(entry);

        return packet;
    }

    public static PlayerListS2CPacket getUpdateDisplayNamePacket(ServerPlayerEntity player, boolean gray) {
        PlayerListS2CPacket packet = new PlayerListS2CPacket();

        PlayerListS2CPacket.Entry entry = packet.new Entry(player.getGameProfile(), 0, player.interactionManager.getGameMode(), getDisplayName(player, gray));
        ((PlayerListS2CPacketAccessor) packet).nucleoid$setAction(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        ((PlayerListS2CPacketAccessor) packet).nucleoid$getEntries().add(entry);

        return packet;
    }

    public static PlayerListS2CPacket getAddPlayerPacket(ServerPlayerEntity player, boolean gray) {
        PlayerListS2CPacket packet = new PlayerListS2CPacket();

        PlayerListS2CPacket.Entry entry = packet.new Entry(player.getGameProfile(), 0, getGameMode(player, gray), getDisplayName(player, gray));
        ((PlayerListS2CPacketAccessor) packet).nucleoid$setAction(PlayerListS2CPacket.Action.ADD_PLAYER);
        ((PlayerListS2CPacketAccessor) packet).nucleoid$getEntries().add(entry);

        return packet;
    }

    public static boolean shouldGray(ServerPlayerEntity left, ServerPlayerEntity right) {
        return ManagedGameSpace.forWorld(left.world) != ManagedGameSpace.forWorld(right.world);
    }

    public static void updatePlayer(ServerPlayerEntity updatedPlayer) {
        updatePlayer(updatedPlayer, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        updatePlayer(updatedPlayer, PlayerListS2CPacket.Action.UPDATE_GAME_MODE);
    }

    private static void updatePlayer(ServerPlayerEntity updatedPlayer, PlayerListS2CPacket.Action action) {
        MinecraftServer server = updatedPlayer.server;

        PlayerListS2CPacket normalPacket = PlayerListHelper.getUpdateDisplayNamePacket(updatedPlayer, false);
        PlayerListS2CPacket grayPacket = PlayerListHelper.getUpdateDisplayNamePacket(updatedPlayer, true);

        PlayerListS2CPacket updateJoined = new PlayerListS2CPacket();
        ((PlayerListS2CPacketAccessor) updateJoined).nucleoid$setAction(action);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean gray = PlayerListHelper.shouldGray(player, updatedPlayer);

            player.networkHandler.sendPacket(gray ? grayPacket : normalPacket);
            PlayerListS2CPacket.Entry entry = updateJoined.new Entry(
                    player.getGameProfile(),
                    0,
                    getGameMode(player, gray),
                    PlayerListHelper.getDisplayName(player, gray)
            );
            ((PlayerListS2CPacketAccessor) updateJoined).nucleoid$getEntries().add(entry);
        }

        updatedPlayer.networkHandler.sendPacket(updateJoined);
    }
}
