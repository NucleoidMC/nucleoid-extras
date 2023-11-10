package xyz.nucleoid.extras.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickTickLog(J)V")
    )
    public void pushTickPerformanceLog(MinecraftServer server, long nanos, Operation<Void> operation) {
        this.extras$tickPerformanceLog.push(nanos);
    }

    @Override
    public PerformanceLog getTickPerformanceLog() {
        return this.extras$tickPerformanceLog;
    }
}
