package xyz.nucleoid.extras.mixin.datafixer;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.schemas.V1460;

@Mixin(V1460.class)
public abstract class Schema1460Mixin extends Schema {
    public Schema1460Mixin(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Shadow protected static void registerMob(Schema schema, Map<String, Supplier<TypeTemplate>> map, String entityId) {};

    @Inject(method = "registerEntities", at = @At("RETURN"))
    private void registerCustomEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        var map = cir.getReturnValue();

        registerMob(schema, map, mod("quick_armor_stand"));
        registerSimple(map, mod("leaderboard_display"));
    }

    @Unique
    private static String mod(String path) {
        return "nucleoid_extras:" + path;
    }
}
