package xyz.nucleoid.extras.player_list;

import eu.pb4.polymer.core.mixin.entity.PlayerListS2CPacketAccessor;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PlayerListHelper {
    public static Text getDisplayName(ServerPlayerEntity player, boolean gray) {
        return gray ? player.getName().copy().formatted(Formatting.DARK_GRAY, Formatting.ITALIC) : player.getDisplayName();
    }

    @Nullable
    private static PublicPlayerSession.Serialized getSession(ServerPlayerEntity player) {
        return Nullables.map(player.getSession(), PublicPlayerSession::toSerialized);
    }

    public static GameMode getGameMode(ServerPlayerEntity player, boolean gray) {
        return gray ? GameMode.SPECTATOR : player.interactionManager.getGameMode();
    }

    public static PlayerListS2CPacket.Entry createEntry(ServerPlayerEntity player,  boolean gray) {
        return new PlayerListS2CPacket.Entry(
            player.getUuid(),
            player.getGameProfile(),
            true,
            player.pingMilliseconds,
            getGameMode(player, gray),
            getDisplayName(player, gray),
            getSession(player)
        );
    }

    private static PlayerListS2CPacket createPacket(EnumSet<PlayerListS2CPacket.Action> actions, List<PlayerListS2CPacket.Entry> entries) {
        PlayerListS2CPacket packet = new PlayerListS2CPacket(actions, List.of());
        ((PlayerListS2CPacketAccessor) packet).setEntries(entries);
        return packet;
    }

    public static PlayerListS2CPacket createPacket(ServerPlayerEntity player, EnumSet<PlayerListS2CPacket.Action> actions, boolean gray) {
        return createPacket(actions, List.of(createEntry(player, gray)));
    }

    public static PlayerListS2CPacket createAddPacket(ServerPlayerEntity player, boolean gray) {
        final EnumSet<PlayerListS2CPacket.Action> actions = EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.INITIALIZE_CHAT, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        return createPacket(player, actions, gray);
    }

    public static boolean shouldGray(ServerPlayerEntity left, ServerPlayerEntity right) {
        var manager = GameSpaceManager.get();
        return manager.byWorld(left.getWorld()) != manager.byWorld(right.getWorld());
    }

    public static void updatePlayer(ServerPlayerEntity updatedPlayer) {
        updatePlayer(updatedPlayer, EnumSet.of(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, PlayerListS2CPacket.Action.UPDATE_GAME_MODE));
    }

    private static void updatePlayer(ServerPlayerEntity updatedPlayer, EnumSet<PlayerListS2CPacket.Action> actions) {
        var server = updatedPlayer.server;

        var normalPacket = PlayerListHelper.createPacket(updatedPlayer, actions, false);
        var grayPacket = PlayerListHelper.createPacket(updatedPlayer, actions, true);

        var updateJoined = new ArrayList<PlayerListS2CPacket.Entry>();

        for (var player : server.getPlayerManager().getPlayerList()) {
            boolean gray = PlayerListHelper.shouldGray(player, updatedPlayer);

            player.networkHandler.sendPacket(gray ? grayPacket : normalPacket);
            updateJoined.add(createEntry(player, gray));
        }

        updatedPlayer.networkHandler.sendPacket(createPacket(actions, updateJoined));
    }
}
