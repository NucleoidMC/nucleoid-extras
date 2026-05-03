package xyz.nucleoid.extras.game_portal;

import com.google.common.io.ByteStreams;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.network.BungeeCordPayload;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.impl.portal.GamePortalBackend;
import xyz.nucleoid.plasmid.impl.portal.GamePortalDisplay;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ServerChangePortalBackend implements GamePortalBackend {
    public static final Map<String, List<ServerChangePortalBackend>> ID_TO_PORTAL = new HashMap<>();
    private static boolean lastFailed = false;

    private final ItemStack icon;
    private final Component name;
    private final MutableComponent hologramName;
    private final List<Component> description;
    private final String serverId;
    private int cachedPlayerCount = 0;
    private int lastUpdate = 0;
    private boolean waitingForUpdate;

    public ServerChangePortalBackend(Component name, List<Component> description, ItemStack icon, String serverId) {
        this.name = name;
        var hologramName = name.copy();

        if (hologramName.getStyle().getColor() == null) {
            hologramName.setStyle(hologramName.getStyle().withColor(ChatFormatting.AQUA));
        }

        this.hologramName = hologramName;
        this.description = description;
        this.icon = icon;

        this.serverId = serverId;
    }

    public static void tick(MinecraftServer server) {
        try {
            var players = server.getPlayerList().getPlayers();

            if (players.isEmpty() || server.getTickCount() % 200 != 0) {
                return;
            }

            var random = players.get(RandomSource.create().nextInt(players.size()));

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
    public Component getName() {
        return this.name;
    }

    @Override
    public List<Component> getDescription() {
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
    public void applyTo(ServerPlayer player, boolean alt) {
        var buf = ByteStreams.newDataOutput();
        buf.writeUTF("Connect");
        buf.writeUTF(this.serverId);
        ServerPlayNetworking.send(player, new BungeeCordPayload(buf.toByteArray()));
    }
}
