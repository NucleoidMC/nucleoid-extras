package xyz.nucleoid.extras.integrations.connection;

import com.google.gson.JsonObject;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class IntegrationsProxy implements IntegrationsConnection {
    private static final long RECONNECT_INTERVAL_MS = 30 * 1000;

    private final SocketAddress address;
    private final Handler receiver;

    private IntegrationsConnection connection;
    private boolean connecting;

    private long lastConnectTime;

    public IntegrationsProxy(InetSocketAddress address, IntegrationsConnection.Handler handler) {
        this.address = address;
        this.receiver = new Handler(handler);
        this.initiateConnection();
    }

    public void tick() {
        if (this.connection == null && !this.connecting) {
            long time = System.currentTimeMillis();
            this.tickDisconnected(time);
        }
    }

    private void tickDisconnected(long time) {
        if (time - this.lastConnectTime > RECONNECT_INTERVAL_MS) {
            this.initiateConnection();
        }
    }

    private void initiateConnection() {
        this.connecting = true;
        this.lastConnectTime = System.currentTimeMillis();

        IntegrationsSocketConnection.connect(this.address, this.receiver).handle((connection, throwable) -> {
            if (connection != null) {
                this.onConnectionOk(connection);
            } else {
                this.onConnectionError(throwable);
            }
            return null;
        });
    }

    private void onConnectionOk(IntegrationsConnection connection) {
        NucleoidIntegrations.LOGGER.info("Successfully opened nucleoid integrations connection to {}", this.address);
        this.connection = connection;
    }

    private void onConnectionError(Throwable throwable) {
        NucleoidIntegrations.LOGGER.error("Failed to open nucleoid integrations connection to {}", this.address, throwable);
        this.closeConnection();
    }

    private void closeConnection() {
        this.connection = null;
        this.connecting = false;
        this.lastConnectTime = System.currentTimeMillis();
    }

    @Override
    public boolean send(String type, JsonObject body) {
        var connection = this.connection;
        if (connection != null) {
            return connection.send(type, body);
        } else {
            return false;
        }
    }

    private class Handler implements IntegrationsConnection.Handler {
        private final IntegrationsConnection.Handler delegate;

        private Handler(IntegrationsConnection.Handler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void acceptConnection() {
            this.delegate.acceptConnection();
        }

        @Override
        public void acceptMessage(String type, JsonObject body) {
            this.delegate.acceptMessage(type, body);
        }

        @Override
        public void acceptError(Throwable cause) {
            this.delegate.acceptError(cause);
            IntegrationsProxy.this.closeConnection();
        }

        @Override
        public void acceptClosed() {
            this.delegate.acceptClosed();
            IntegrationsProxy.this.closeConnection();
        }
    }
}
