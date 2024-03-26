package xyz.nucleoid.extras.placeholder;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

public class ExtraPlaceholders {
    public static void register() {
        Placeholders.register(NucleoidExtras.identifier("location"), ExtraPlaceholders::locationDifference);
    }

    private static PlaceholderResult locationDifference(PlaceholderContext context, @Nullable String s) {
        if (context.hasWorld()) {
            return PlaceholderResult.value(MutableText.of(new GameTextContent(GameSpaceManager.get().byWorld(context.world()))));
        }

        return PlaceholderResult.value(Text.empty());
    }
}
