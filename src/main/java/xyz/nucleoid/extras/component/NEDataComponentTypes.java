package xyz.nucleoid.extras.component;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import xyz.nucleoid.extras.NucleoidExtras;

public final class NEDataComponentTypes {
    private NEDataComponentTypes() {
    }

    public static final ComponentType<LauncherComponent> LAUNCHER = register("launcher", ComponentType.<LauncherComponent>builder()
            .codec(LauncherComponent.CODEC)
            .cache()
            .build());

    public static final ComponentType<GamePortalComponent> GAME_PORTAL = register("game_portal", ComponentType.<GamePortalComponent>builder()
            .codec(GamePortalComponent.CODEC)
            .cache()
            .build());

    public static final ComponentType<TaterPositionsComponent> TATER_POSITIONS = register("tater_positions", ComponentType.<TaterPositionsComponent>builder()
            .codec(TaterPositionsComponent.CODEC)
            .cache()
            .build());

    public static final ComponentType<TaterSelectionComponent> TATER_SELECTION = register("tater_selection", ComponentType.<TaterSelectionComponent>builder()
            .codec(TaterSelectionComponent.CODEC)
            .cache()
            .build());

    private static <T> ComponentType<T> register(String path, ComponentType<T> type) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, NucleoidExtras.identifier(path), type);
    }

    public static void register() {
        PolymerComponent.registerDataComponent(LAUNCHER);
        PolymerComponent.registerDataComponent(GAME_PORTAL);
        PolymerComponent.registerDataComponent(TATER_POSITIONS);
        PolymerComponent.registerDataComponent(TATER_SELECTION);
    }
}
