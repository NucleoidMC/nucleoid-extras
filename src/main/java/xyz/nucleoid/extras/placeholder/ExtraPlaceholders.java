package xyz.nucleoid.extras.placeholder;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

public class ExtraPlaceholders {
    public static void register() {
        Placeholders.registerServer(NucleoidExtras.identifier("location"), ExtraPlaceholders::locationDifference);
    }

    private static PlaceholderResult locationDifference(PlaceholderContext context, @Nullable String s) {
        if (context.hasLevel()) {
            return PlaceholderResult.value(MutableComponent.create(new GameTextContent(GameSpaceManager.get().byLevel(context.level()))));
        }

        return PlaceholderResult.value(Component.empty());
    }
}
