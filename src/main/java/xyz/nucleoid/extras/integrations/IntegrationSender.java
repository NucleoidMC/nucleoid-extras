package xyz.nucleoid.extras.integrations;

import com.google.gson.JsonObject;

public interface IntegrationSender {
    boolean send(JsonObject body);
}
