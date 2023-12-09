package xyz.nucleoid.extras.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;
import xyz.nucleoid.extras.integrations.status.HasTickPerformanceLog;
import xyz.nucleoid.extras.integrations.status.ServerLifecycleIntegration;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements HasTickPerformanceLog {
    @Unique
    private final PerformanceLog extras$tickPerformanceLog = new PerformanceLog();

    @ModifyArg(
            method = "runServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setCrashReport(Lnet/minecraft/util/crash/CrashReport;)V")
    )
    private CrashReport extras$onServerCrash(CrashReport report) {
        if (report != null) {
            ExtrasErrorReporter.onServerCrash(report);
            ServerLifecycleIntegration.setCrashed();
        }
        return report;
    }

    @Inject(
            method = "tickTickLog",
            at = @At(value = "HEAD")
    )
    public void pushTickPerformanceLog(long nanos, CallbackInfo ci) {
        this.extras$tickPerformanceLog.push(nanos);
    }

    @Override
    public PerformanceLog getTickPerformanceLog() {
        return this.extras$tickPerformanceLog;
    }
}
