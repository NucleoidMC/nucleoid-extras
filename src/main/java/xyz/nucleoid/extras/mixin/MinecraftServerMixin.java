package xyz.nucleoid.extras.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;
import xyz.nucleoid.extras.integrations.status.ServerLifecycleIntegration;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyArg(
            method = "runServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setCrashReport(Lnet/minecraft/util/crash/CrashReport;)V")
    )
    private CrashReport onServerCrash(CrashReport report) {
        if (report != null) {
            ExtrasErrorReporter.onServerCrash(report);
            ServerLifecycleIntegration.setCrashed();
        }
        return report;
    }
}
