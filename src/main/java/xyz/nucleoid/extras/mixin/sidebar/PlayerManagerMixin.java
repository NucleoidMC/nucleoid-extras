package xyz.nucleoid.extras.mixin.sidebar;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void extras$onPlayerLeave(ServerPlayerEntity player, CallbackInfo ci) {
        if (player.getWorld().getRegistryKey() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.get().removePlayer(player);
        }
    }
}
