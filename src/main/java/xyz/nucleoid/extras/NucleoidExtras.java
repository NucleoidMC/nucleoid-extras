package xyz.nucleoid.extras;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.chat_filter.ChatFilter;
import xyz.nucleoid.extras.command.CommandAliases;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;
import xyz.nucleoid.extras.integrations.NucleoidIntegrations;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.NECriteria;
import xyz.nucleoid.extras.lobby.NEEntities;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.extras.scheduled_stop.ScheduledStop;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;

public final class NucleoidExtras implements ModInitializer {
    public static final String ID = "nucleoid_extras";
    public static final Logger LOGGER = LogManager.getLogger(NucleoidExtras.class);

    @Override
    public void onInitialize() {
        NEBlocks.register();
        NEItems.register();
        NEEntities.register();
        NECriteria.register();

        ChatFilter.register();
        CommandAliases.register();
        ScheduledStop.register();

        NucleoidIntegrations.register();

        ExtrasErrorReporter.register();

        ServerTickEvents.END_SERVER_TICK.register(NucleoidExtras::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        int ticks = server.getTicks();
        var config = NucleoidExtrasConfig.get();
        if (config.sidebar()) {
            NucleoidSidebar.get().update(ticks, server, config);
        }

        var integrations = NucleoidIntegrations.get();
        if (integrations != null) {
            integrations.tick();
        }
    }

    public static Identifier identifier(String path) {
        return new Identifier(ID, path);
    }

    @Nullable
    public static String getChatMessageContent(Text message) {
        if (message instanceof TranslatableText translatable) {
            var args = translatable.getArgs();
            if (args.length == 2) {
                var content = args[1];
                if (content instanceof String string) {
                    return string;
                } else if (content instanceof StringVisitable visitable) {
                    return visitable.getString();
                }
            }
        } else {
            return message.getString();
        }
        return null;
    }
}
