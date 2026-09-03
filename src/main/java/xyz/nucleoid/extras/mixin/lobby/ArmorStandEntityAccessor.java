package xyz.nucleoid.extras.mixin.lobby;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public interface ArmorStandEntityAccessor {
    @Invoker("getSlotFromPosition")
    EquipmentSlot callSlotFromPosition(Vec3d hitPos);

    @Invoker("setHideBasePlate")
    void callSetHideBasePlate(boolean hideBasePlate);

    @Invoker("setShowArms")
    void callSetShowArms(boolean showArms);
}
