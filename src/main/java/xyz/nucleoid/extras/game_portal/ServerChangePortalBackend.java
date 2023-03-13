package xyz.nucleoid.extras.game_portal;

import com.google.common.io.ByteStreams;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.game.portal.GamePortalDisplay;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ServerChangePortalBackend implements GamePortalBackend {
    private static final Identifier PACKET_ID = new Identifier("bungeecord", "main");

    public static final Map<String, List<ServerChangePortalBackend>> ID_TO_PORTAL = new HashMap<>();

    private final ItemStack icon;
    private final Text name;
    private final MutableText hologramName;
    private final List<Text> description;
    private final String serverId;
    private int cachedPlayerCount = 0;
    private int lastUpdate = 0;
    private boolean waitingForUpdate;

    public ServerChangePortalBackend(Text name, List<Text> description, ItemStack icon, String serverId) {
        this.name = name;
        var hologramName = name.copy();

        if (hologramName.getStyle().getColor() == null) {
            hologramName.setStyle(hologramName.getStyle().withColor(Formatting.AQUA));
        }

        this.hologramName = hologramName;
        this.description = description;
        this.icon = icon;

        this.serverId = serverId;
    }

    public static void tick(MinecraftServer server) {
        var players = server.getPlayerManager().getPlayerList();

        if (players.isEmpty() || server.getTicks() % 100 != 0) {
            return;
        }

        var random = players.get(Random.create().nextInt(players.size()));

        for (var key : ID_TO_PORTAL.keySet()) {
            var buf = ByteStreams.newDataOutput();
            buf.writeUTF("PlayerCount");
            buf.writeUTF(key);

            var out = PacketByteBufs.create();
            out.writeBytes(buf.toByteArray());
            random.networkHandler.sendPacket(new CustomPayloadS2CPacket(PACKET_ID, out));
        }
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, ServerChangePortalBackend::handlePacket);
    }

    private static void handlePacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        try {
            var out = new DataInputStream(new InputStream() {
                @Override
                public int read() throws IOException {
                    return buf.readByte();
                }
            });

            var type = out.readUTF();

            if (type.equals("PlayerCount")) {
                var serverId = out.readUTF();
                var count = out.readInt();

                server.execute(() -> {
                    var x = ID_TO_PORTAL.get(serverId);

                    if (x != null) {
                        for (var y : x) {
                            y.setPlayerCount(count);
                        }
                        x.clear();
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public List<Text> getDescription() {
        return this.description;
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public int getPlayerCount() {
        if (!this.waitingForUpdate && System.currentTimeMillis() - this.lastUpdate > 10 * 1000) {
            this.waitingForUpdate = true;
            var list = ID_TO_PORTAL.get(this.serverId);
            if (list == null) {
                list = new ArrayList<>();
                ID_TO_PORTAL.put(this.serverId, list);
            }
            list.add(this);
        }

        return this.cachedPlayerCount;
    }

    public void setPlayerCount(int count) {
        this.cachedPlayerCount = count;
        this.waitingForUpdate = false;
        this.lastUpdate = (int) System.currentTimeMillis();
    }

    @Override
    public void provideGameSpaces(Consumer<GameSpace> consumer) {
    }

    @Override
    public void populateDisplay(GamePortalDisplay display) {
        display.set(GamePortalDisplay.NAME, this.hologramName);
        display.set(GamePortalDisplay.PLAYER_COUNT, this.getPlayerCount());
    }

    @Override
    public void applyTo(ServerPlayerEntity player) {
        var buf = ByteStreams.newDataOutput();
        buf.writeUTF("Connect");
        buf.writeUTF(this.serverId);


        var out = PacketByteBufs.create();
        out.writeBytes(buf.toByteArray());
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(PACKET_ID, out));
    }
}
