package xyz.nucleoid.extras.integrations;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtrasConfig;
import xyz.nucleoid.extras.integrations.connection.IntegrationsConnection;
import xyz.nucleoid.extras.integrations.connection.IntegrationsProxy;
import xyz.nucleoid.extras.integrations.game.GameStatusIntegration;
import xyz.nucleoid.extras.integrations.game.StatisticsIntegration;
import xyz.nucleoid.extras.integrations.relay.ChatRelayIntegration;
import xyz.nucleoid.extras.integrations.relay.RemoteCommandIntegration;
import xyz.nucleoid.extras.integrations.status.PlayerStatusIntegration;
import xyz.nucleoid.extras.integrations.status.ServerLifecycleIntegration;
import xyz.nucleoid.extras.integrations.status.ServerPerformanceIntegration;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class NucleoidIntegrations {
    public static final Logger LOGGER = LogManager.getLogger(NucleoidIntegrations.class);

    private static NucleoidIntegrations instance;
    private static boolean initialized;

    private final IntegrationsConfig config;
    private final IntegrationsProxy proxy;

    private final Map<String, Consumer<JsonObject>> messageReceivers = new Object2ObjectOpenHashMap<>();
    private final List<Runnable> connectionOpenListeners = new ArrayList<>();

    private NucleoidIntegrations(IntegrationsConfig config) {
        this.config = config;

        NucleoidIntegrations.bindIntegrations(this, config);

        var address = new InetSocketAddress(config.host(), config.port());
        this.proxy = new IntegrationsProxy(address, new IntegrationsConnection.Handler() {
            @Override
            public void acceptConnection() {
                NucleoidIntegrations.this.handleConnection();
            }

            @Override
            public void acceptMessage(String type, JsonObject body) {
                NucleoidIntegrations.this.handleMessage(type, body);
            }

            @Override
            public void acceptError(Throwable cause) {
                NucleoidIntegrations.LOGGER.error("Nucleoid integrations connection gave error", cause);
            }

            @Override
            public void acceptClosed() {
            }
        });
    }

    private static void bindIntegrations(NucleoidIntegrations integrations, IntegrationsConfig config) {
        ChatRelayIntegration.bind(integrations, config);
        PlayerStatusIntegration.bind(integrations, config);
        GameStatusIntegration.bind(integrations, config);
        ServerLifecycleIntegration.bind(integrations, config);
        ServerPerformanceIntegration.bind(integrations, config);
        RemoteCommandIntegration.bind(integrations, config);
        StatisticsIntegration.bind(integrations, config);
    }

    public static void register() {
        initialized = true;
        var config = NucleoidExtrasConfig.get().integrations();
        instance = config != null ? new NucleoidIntegrations(config) : null;
    }

    @Nullable
    public static NucleoidIntegrations get() {
        Preconditions.checkState(initialized, "integrations not initialized");
        return instance;
    }

    public void bindReceiver(String type, Consumer<JsonObject> receiver) {
        if (this.messageReceivers.putIfAbsent(type, receiver) != null) {
            throw new IllegalArgumentException("duplicate receiver bound for " + type);
        }
    }

    public void bindConnectionOpen(Runnable runnable) {
        this.connectionOpenListeners.add(runnable);
    }

    public IntegrationSender openSender(String type) {
        return body -> this.proxy.send(type, body);
    }

    public void tick() {
        this.proxy.tick();
    }

    void handleConnection() {
        var body = new JsonObject();
        body.addProperty("channel", this.config.channel());
        body.addProperty("game_version", SharedConstants.getGameVersion().getName());

        var serverIp = this.config.serverIp();
        if (serverIp != null) {
            body.addProperty("server_ip", serverIp);
        }

        this.proxy.send("handshake", body);

        for (var listener : this.connectionOpenListeners) {
            listener.run();
        }
    }

    void handleMessage(String type, JsonObject body) {
        var handler = this.messageReceivers.get(type);
        if (handler != null) {
            handler.accept(body);
        } else {
            LOGGER.warn("Missing handler for message type: {}", type);
        }
    }
}
