package xyz.nucleoid.extras.player_list;

import eu.pb4.polymer.core.mixin.entity.PlayerListS2CPacketAccessor;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class PlayerListHelper {
    public static Component getDisplayName(ServerPlayer player, boolean gray) {
        return gray ? player.getName().copy().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC) : player.getDisplayName();
    }

    @Nullable
    private static RemoteChatSession.Data getSession(ServerPlayer player) {
        return Optionull.map(player.getChatSession(), RemoteChatSession::asData);
    }

    public static GameType getGameMode(ClientboundPlayerInfoUpdatePacket.Entry examplar, boolean gray) {
        return gray ? GameType.SPECTATOR : examplar.gameMode();
    }

    public static ClientboundPlayerInfoUpdatePacket.Entry createEntry(ServerPlayer player,  boolean gray) {
        var examplar = new ClientboundPlayerInfoUpdatePacket.Entry(player);

        return new ClientboundPlayerInfoUpdatePacket.Entry(
            examplar.profileId(),
            examplar.profile(),
            examplar.listed(),
            examplar.latency(),
            getGameMode(examplar, gray),
            getDisplayName(player, gray),
            examplar.showHat(),
            examplar.listOrder(),
            examplar.chatSession()
        );
    }

    private static ClientboundPlayerInfoUpdatePacket createPacket(EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions, List<ClientboundPlayerInfoUpdatePacket.Entry> entries) {
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(actions, List.of());
        ((PlayerListS2CPacketAccessor) packet).setEntries(entries);
        return packet;
    }

    public static ClientboundPlayerInfoUpdatePacket createPacket(ServerPlayer player, EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions, boolean gray) {
        return createPacket(actions, List.of(createEntry(player, gray)));
    }

    public static ClientboundPlayerInfoUpdatePacket createAddPacket(ServerPlayer player, boolean gray) {
        final EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        return createPacket(player, actions, gray);
    }

    public static boolean shouldGray(ServerPlayer left, ServerPlayer right) {
        var manager = GameSpaceManager.get();
        return manager.byWorld(left.level()) != manager.byWorld(right.level());
    }

    public static void updatePlayer(ServerPlayer updatedPlayer) {
        updatePlayer(updatedPlayer, EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE));
    }

    private static void updatePlayer(ServerPlayer updatedPlayer, EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions) {
        var server = updatedPlayer.level().getServer();

        var normalPacket = PlayerListHelper.createPacket(updatedPlayer, actions, false);
        var grayPacket = PlayerListHelper.createPacket(updatedPlayer, actions, true);

        var updateJoined = new ArrayList<ClientboundPlayerInfoUpdatePacket.Entry>();

        for (var player : server.getPlayerList().getPlayers()) {
            boolean gray = PlayerListHelper.shouldGray(player, updatedPlayer);

            player.connection.send(gray ? grayPacket : normalPacket);
            updateJoined.add(createEntry(player, gray));
        }

        updatedPlayer.connection.send(createPacket(actions, updateJoined));
    }
}
