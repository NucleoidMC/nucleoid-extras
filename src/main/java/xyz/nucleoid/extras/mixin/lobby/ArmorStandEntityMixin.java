package xyz.nucleoid.extras.mixin.lobby;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.extras.lobby.item.TaterBoxItem;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin {
    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void interactAt(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        if (!player.getWorld().isClient()) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof TaterBoxItem taterBox) {
                ci.setReturnValue(taterBox.tryAdd((Entity) (Object) this, hitPos, stack, player));
            }
        }
    }
}
