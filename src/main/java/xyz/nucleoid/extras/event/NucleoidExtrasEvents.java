package xyz.nucleoid.extras.event;

import net.minecraft.server.MinecraftServer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class NucleoidExtrasEvents {
    private NucleoidExtrasEvents() {
    }

    /**
     * Called at the end of the server tick, including when the server is paused (compared to {@link ServerTickEvents#END_SERVER_TICK}).
     */
    public static final Event<EndTick> END_SERVER_TICK = EventFactory.createArrayBacked(EndTick.class, listeners -> server -> {
        for (var listener : listeners) {
            listener.onEndTick(server);
        }
    });

    @FunctionalInterface
    public interface EndTick {
        void onEndTick(MinecraftServer server);
    }
}
