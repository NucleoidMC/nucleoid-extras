package xyz.nucleoid.sidebar.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.sidebar.NucleoidSidebar;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerLeave(ServerPlayerEntity player, CallbackInfo ci) {
        if (player.world.getRegistryKey() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.removePlayer(player);
        }
    }
}
