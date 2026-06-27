package xyz.nucleoid.extras.model;

import net.minecraft.resources.Identifier;
import xyz.nucleoid.extras.NucleoidExtras;

public class NEModels {
    public static final Identifier CONTROLLER = of("controller");

    private static Identifier of(String path) {
        return NucleoidExtras.identifier(path);
    }
}
