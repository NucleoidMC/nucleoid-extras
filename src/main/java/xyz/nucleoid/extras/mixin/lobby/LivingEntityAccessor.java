package xyz.nucleoid.extras.mixin.lobby;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getEquipmentChanges")
    Map<EquipmentSlot, ItemStack> callGetEquipmentChanges();

    @Invoker("setSyncedHandStack")
    void callSetSyncedHandStack(EquipmentSlot slot, ItemStack stack);

    @Invoker("setSyncedArmorStack")
    void callSetSyncedArmorStack(EquipmentSlot slot, ItemStack stack);
}
