package xyz.nucleoid.extras;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.extras.mixin.player_list.PlayerListS2CPacketAccessor;
import xyz.nucleoid.plasmid.game.ManagedGameSpace;

public class PlayerListHelper {
    public static Text getDisplayName(ServerPlayerEntity player, boolean gray) {
        Text displayName = player.getDisplayName();
        return gray ? displayName.shallowCopy().formatted(Formatting.DARK_GRAY, Formatting.ITALIC) : displayName;
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

        PlayerListS2CPacket.Entry entry = packet.new Entry(player.getGameProfile(), 0, player.interactionManager.getGameMode(), getDisplayName(player, gray));
        ((PlayerListS2CPacketAccessor) packet).nucleoid$setAction(PlayerListS2CPacket.Action.ADD_PLAYER);
        ((PlayerListS2CPacketAccessor) packet).nucleoid$getEntries().add(entry);

        return packet;
    }

    public static boolean shouldGray(ServerPlayerEntity left, ServerPlayerEntity right) {
        return ManagedGameSpace.forWorld(left.world) != ManagedGameSpace.forWorld(right.world);
    }
}
