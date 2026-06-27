package xyz.nucleoid.extras.mixin.lobby;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandEntityAccessor {
    @Invoker("getClickedSlot")
    EquipmentSlot callSlotFromPosition(Vec3 hitPos);

    @Invoker("setNoBasePlate")
    void callSetHideBasePlate(boolean hideBasePlate);

    @Invoker("setShowArms")
    void callSetShowArms(boolean showArms);
}
