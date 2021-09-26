package xyz.nucleoid.extras.player_list;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

public class PlayerListHelper {
    public static Text getDisplayName(ServerPlayerEntity player, boolean gray) {
        var displayName = player.getDisplayName();
        return gray ? displayName.shallowCopy().formatted(Formatting.DARK_GRAY, Formatting.ITALIC) : displayName;
    }

    public static GameMode getGameMode(ServerPlayerEntity player, boolean gray) {
        return gray ? GameMode.SPECTATOR : player.interactionManager.getGameMode();
    }

    public static PlayerListS2CPacket getUpdatePacket(ServerPlayerEntity player, PlayerListS2CPacket.Action action, boolean gray) {
        var packet = new PlayerListS2CPacket(action);
        packet.getEntries().add(new PlayerListS2CPacket.Entry(player.getGameProfile(), 0, getGameMode(player, gray), getDisplayName(player, gray)));

        return packet;
    }

    public static PlayerListS2CPacket getAddPlayerPacket(ServerPlayerEntity player, boolean gray) {
        var packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER);
        packet.getEntries().add(new PlayerListS2CPacket.Entry(player.getGameProfile(), 0, getGameMode(player, gray), getDisplayName(player, gray)));

        return packet;
    }

    public static boolean shouldGray(ServerPlayerEntity left, ServerPlayerEntity right) {
        var manager = GameSpaceManager.get();
        return manager.byWorld(left.getServerWorld()) != manager.byWorld(right.getServerWorld());
    }

    public static void updatePlayer(ServerPlayerEntity updatedPlayer) {
        updatePlayer(updatedPlayer, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        updatePlayer(updatedPlayer, PlayerListS2CPacket.Action.UPDATE_GAME_MODE);
    }

    private static void updatePlayer(ServerPlayerEntity updatedPlayer, PlayerListS2CPacket.Action action) {
        var server = updatedPlayer.server;

        var normalPacket = PlayerListHelper.getUpdatePacket(updatedPlayer, action, false);
        var grayPacket = PlayerListHelper.getUpdatePacket(updatedPlayer, action, true);

        var updateJoined = new PlayerListS2CPacket(action);

        for (var player : server.getPlayerManager().getPlayerList()) {
            boolean gray = PlayerListHelper.shouldGray(player, updatedPlayer);

            player.networkHandler.sendPacket(gray ? grayPacket : normalPacket);
            var entry = new PlayerListS2CPacket.Entry(
                    player.getGameProfile(),
                    0,
                    getGameMode(player, gray),
                    PlayerListHelper.getDisplayName(player, gray)
            );
            updateJoined.getEntries().add(entry);
        }

        updatedPlayer.networkHandler.sendPacket(updateJoined);
    }
}
