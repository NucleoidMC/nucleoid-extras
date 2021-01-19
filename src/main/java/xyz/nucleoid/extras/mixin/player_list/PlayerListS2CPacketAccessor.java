package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerListS2CPacket.class)
public interface PlayerListS2CPacketAccessor {
    @Accessor("entries")
    List<PlayerListS2CPacket.Entry> nucleoid$getEntries();

    @Accessor("action")
    PlayerListS2CPacket.Action nucleoid$getAction();

    @Accessor("action")
    void nucleoid$setAction(PlayerListS2CPacket.Action action);
}
