package xyz.nucleoid.extras.dialog;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.Dialog;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.NucleoidExtrasConfig;

import java.util.Optional;

public class NEDialogs {
    public static final ResourceKey<Dialog> RULES = of("rules");

    private static final RegistrationInfo INFO = new RegistrationInfo(Optional.empty(), Lifecycle.stable());

    public static void register(WritableRegistry<Dialog> registry) {
        var rules = NucleoidExtrasConfig.get().rules();

        if (rules != null) {
            registry.register(RULES, rules.createDialog(), INFO);
        }
    }

    private static ResourceKey<Dialog> of(String path) {
        return ResourceKey.create(Registries.DIALOG, NucleoidExtras.identifier(path));
    }
}
