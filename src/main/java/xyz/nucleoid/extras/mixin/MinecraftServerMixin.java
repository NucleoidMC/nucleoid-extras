package xyz.nucleoid.extras.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.integrations.status.ServerLifecycleIntegration;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setCrashReport(Lnet/minecraft/util/crash/CrashReport;)V"))
    private void onServerCrash(CallbackInfo ci) {
        ServerLifecycleIntegration.setCrashed();
    }
}
