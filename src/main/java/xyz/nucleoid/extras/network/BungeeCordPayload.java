package xyz.nucleoid.extras.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BungeeCordPayload(byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BungeeCordPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("bungeecord", "main"));

    public static final StreamCodec<ByteBuf, BungeeCordPayload> PACKET_CODEC = StreamCodec.ofMember(BungeeCordPayload::write, BungeeCordPayload::read);

    private void write(ByteBuf buf) {
        buf.writeBytes(this.data);
    }

    @Override
    public CustomPacketPayload.Type<BungeeCordPayload> type() {
        return ID;
    }

    private static BungeeCordPayload read(ByteBuf buf) {
        var data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new BungeeCordPayload(data);
    }
}
