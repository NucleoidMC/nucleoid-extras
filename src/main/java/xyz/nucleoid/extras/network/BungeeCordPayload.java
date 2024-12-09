package xyz.nucleoid.extras.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public record BungeeCordPayload(byte[] data) implements CustomPayload {
    public static final CustomPayload.Id<BungeeCordPayload> ID = new CustomPayload.Id<>(Identifier.of("bungeecord", "main"));

    public static final PacketCodec<ByteBuf, BungeeCordPayload> PACKET_CODEC = PacketCodec.of(BungeeCordPayload::write, BungeeCordPayload::read);

    private void write(ByteBuf buf) {
        buf.writeBytes(this.data);
    }

    @Override
    public CustomPayload.Id<BungeeCordPayload> getId() {
        return ID;
    }

    private static BungeeCordPayload read(ByteBuf buf) {
        var data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new BungeeCordPayload(data);
    }
}
