package xyz.nucleoid.extras.mixin.sidebar;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.sidebar.NucleoidSidebar;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void extras$onPlayerLeave(ServerPlayer player, CallbackInfo ci) {
        if (player.level().dimension() == NucleoidSidebar.DIMENSION) {
            NucleoidSidebar.get().removePlayer(player);
        }
    }
}
