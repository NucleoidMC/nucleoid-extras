package xyz.nucleoid.extras.mixin.lobby;

import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemStackComponentizationFix;
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

    @Inject(method = "fixStack", at = @At("TAIL"))
    private static void fixNucleoidExtrasStack(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, CallbackInfo ci) {
        if (data.itemEquals("nucleoid_extras:game_portal_opener")) {
            data.getAndRemove("GamePortal").result().ifPresent(gamePortalId -> {
                var component = dynamic.emptyMap().set("game_portal_id", gamePortalId);
                data.setComponent("nucleoid_extras:game_portal", component);
            });
        } else if (data.itemMatches(TATER_BOX_ITEMS)) {
            data.getAndRemove("SelectedTater").result().ifPresent(gamePortalId -> {
                var component = dynamic.emptyMap().set("tater", gamePortalId);
                data.setComponent("nucleoid_extras:tater_selection", component);
            });
        } else if (data.itemEquals("nucleoid_extras:tater_guidebook")) {
            data.getAndRemove("tater_positions").result().ifPresent(positions -> {
                var component = dynamic.emptyMap().set("positions", positions);
                data.setComponent("nucleoid_extras:tater_positions", component);
            });
        } else if (data.itemEquals("nucleoid_extras:launch_feather")) {
            Dynamic<?> component = dynamic.emptyMap();

            component = data.moveToComponent("Pitch", component, "pitch");
            component = data.moveToComponent("Power", component, "power");

            if (!component.equals(dynamic.emptyMap())) {
                data.setComponent("nucleoid_extras:launcher", component);
            }
        }
    }
}
