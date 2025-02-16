package xyz.nucleoid.extras.game_portal;

import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.network.BungeeCordPayload;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.GamePortalDisplay;

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
    public static final Map<String, List<ServerChangePortalBackend>> ID_TO_PORTAL = new HashMap<>();
    private static boolean lastFailed = false;

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
        try {
            var players = server.getPlayerManager().getPlayerList();

            if (players.isEmpty() || server.getTicks() % 200 != 0) {
                return;
            }

            var random = players.get(Random.create().nextInt(players.size()));

            for (var key : ID_TO_PORTAL.keySet()) {
                var buf = ByteStreams.newDataOutput();
                buf.writeUTF("PlayerCount");
                buf.writeUTF(key);

                ServerPlayNetworking.send(random, new BungeeCordPayload(buf.toByteArray()));
            }
            lastFailed = false;
        } catch (Throwable e) {
            if (!lastFailed) {
                NucleoidExtras.LOGGER.warn("Failed to sent bungee packet!", e);
            }
            lastFailed = true;
        }
    }

    public static void handlePacket(BungeeCordPayload payload, ServerPlayNetworking.Context context) {
        try {
            var out = new DataInputStream(new ByteArrayInputStream(payload.data()));

            var type = out.readUTF();

            if (type.equals("PlayerCount")) {
                var serverId = out.readUTF();
                var count = out.readInt();

                context.server().execute(() -> {
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
            NucleoidExtras.LOGGER.warn("Failed to receive bungee packet!", e);
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
    public void applyTo(ServerPlayerEntity player, boolean alt) {
        var buf = ByteStreams.newDataOutput();
        buf.writeUTF("Connect");
        buf.writeUTF(this.serverId);
        ServerPlayNetworking.send(player, new BungeeCordPayload(buf.toByteArray()));
    }
}
