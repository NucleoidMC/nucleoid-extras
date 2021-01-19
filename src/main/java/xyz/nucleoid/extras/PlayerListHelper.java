package xyz.nucleoid.extras;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.extras.mixin.PlayerListS2CPacketAccessor;

public class PlayerListHelper {
    public static Text getDisplayName(ServerPlayerEntity player, boolean gray) {
        MutableText displayName = player.getDisplayName() != null ? player.getDisplayName().shallowCopy() : player.getName().shallowCopy();
        return gray ? displayName.formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC) : displayName;
    }

    public static PlayerListS2CPacket getUpdateDisplayNamePacket(ServerPlayerEntity player, boolean gray) {
        PlayerListS2CPacket packet = new PlayerListS2CPacket();

        PlayerListS2CPacket.Entry entry = packet.new Entry(player.getGameProfile(), 0, player.interactionManager.getGameMode(), getDisplayName(player, gray));
        ((PlayerListS2CPacketAccessor) packet).nucleoid$setAction(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        ((PlayerListS2CPacketAccessor) packet).nucleoid$getEntries().add(entry);

        return packet;
    }
}
