package xyz.nucleoid.extras.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import xyz.nucleoid.extras.game_portal.ServerChangePortalBackend;

public final class NucleoidExtrasNetworking {
    private NucleoidExtrasNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(BungeeCordPayload.ID, BungeeCordPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(BungeeCordPayload.ID, BungeeCordPayload.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BungeeCordPayload.ID, ServerChangePortalBackend::handlePacket);
    }
}
