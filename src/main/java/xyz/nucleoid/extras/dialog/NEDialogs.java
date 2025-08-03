package xyz.nucleoid.extras.dialog;

import com.mojang.serialization.Lifecycle;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryInfo;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.NucleoidExtrasConfig;

import java.util.Optional;

public class NEDialogs {
    public static final RegistryKey<Dialog> RULES = of("rules");

    private static final RegistryEntryInfo INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.stable());

    public static void register(MutableRegistry<Dialog> registry) {
        var rules = NucleoidExtrasConfig.get().rules();

        if (rules != null) {
            registry.add(RULES, rules.createDialog(), INFO);
        }
    }

    private static RegistryKey<Dialog> of(String path) {
        return RegistryKey.of(RegistryKeys.DIALOG, NucleoidExtras.identifier(path));
    }
}
