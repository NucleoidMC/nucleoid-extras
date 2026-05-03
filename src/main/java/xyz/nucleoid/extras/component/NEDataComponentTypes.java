package xyz.nucleoid.extras.component;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import xyz.nucleoid.extras.NucleoidExtras;

public final class NEDataComponentTypes {
    private NEDataComponentTypes() {
    }

    public static final DataComponentType<LauncherComponent> LAUNCHER = register("launcher", DataComponentType.<LauncherComponent>builder()
            .persistent(LauncherComponent.CODEC)
            .cacheEncoding()
            .build());

    public static final DataComponentType<GamePortalComponent> GAME_PORTAL = register("game_portal", DataComponentType.<GamePortalComponent>builder()
            .persistent(GamePortalComponent.CODEC)
            .cacheEncoding()
            .build());

    public static final DataComponentType<TaterPositionsComponent> TATER_POSITIONS = register("tater_positions", DataComponentType.<TaterPositionsComponent>builder()
            .persistent(TaterPositionsComponent.CODEC)
            .cacheEncoding()
            .build());

    public static final DataComponentType<TaterSelectionComponent> TATER_SELECTION = register("tater_selection", DataComponentType.<TaterSelectionComponent>builder()
            .persistent(TaterSelectionComponent.CODEC)
            .cacheEncoding()
            .build());

    private static <T> DataComponentType<T> register(String path, DataComponentType<T> type) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, NucleoidExtras.identifier(path), type);
    }

    public static void register() {
        PolymerComponent.registerDataComponent(LAUNCHER);
        PolymerComponent.registerDataComponent(GAME_PORTAL);
        PolymerComponent.registerDataComponent(TATER_POSITIONS);
        PolymerComponent.registerDataComponent(TATER_SELECTION);
    }
}
