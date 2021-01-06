package xyz.nucleoid.extras.integrations.connection;

import com.google.gson.JsonObject;

public interface IntegrationsConnection {
    boolean send(String type, JsonObject body);

    interface Handler {
        void acceptConnection();

        void acceptMessage(String type, JsonObject body);

        void acceptError(Throwable cause);

        void acceptClosed();
    }
}
