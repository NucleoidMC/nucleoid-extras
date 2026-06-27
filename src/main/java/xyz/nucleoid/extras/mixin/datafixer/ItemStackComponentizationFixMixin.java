package xyz.nucleoid.extras.mixin.datafixer;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
    @Unique
    private static final Set<String> TATER_BOX_ITEMS = Set.of(
            "nucleoid_extras:tater_box",
            "nucleoid_extras:creative_tater_box"
    );

    @Inject(method = "fixItemStack", at = @At("TAIL"))
    private static void fixNucleoidExtrasStack(ItemStackComponentizationFix.ItemStackData data, Dynamic<?> dynamic, CallbackInfo ci) {
        if (data.is("nucleoid_extras:game_portal_opener")) {
            data.removeTag("GamePortal").result().ifPresent(gamePortalId -> {
                var component = dynamic.emptyMap().set("game_portal_id", gamePortalId);
                data.setComponent("nucleoid_extras:game_portal", component);
            });
        } else if (data.is(TATER_BOX_ITEMS)) {
            data.removeTag("SelectedTater").result().ifPresent(gamePortalId -> {
                var component = dynamic.emptyMap().set("tater", gamePortalId);
                data.setComponent("nucleoid_extras:tater_selection", component);
            });
        } else if (data.is("nucleoid_extras:tater_guidebook")) {
            data.removeTag("tater_positions").result().ifPresent(positions -> {
                var component = dynamic.emptyMap().set("positions", positions);
                data.setComponent("nucleoid_extras:tater_positions", component);
            });
        } else if (data.is("nucleoid_extras:launch_feather")) {
            Dynamic<?> component = dynamic.emptyMap();

            component = data.moveTagInto("Pitch", component, "pitch");
            component = data.moveTagInto("Power", component, "power");

            if (!component.equals(dynamic.emptyMap())) {
                data.setComponent("nucleoid_extras:launcher", component);
            }
        }
    }
}
