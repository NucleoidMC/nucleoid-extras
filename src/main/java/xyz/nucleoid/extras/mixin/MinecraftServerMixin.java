package xyz.nucleoid.extras.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.error.ExtrasErrorReporter;
import xyz.nucleoid.extras.event.NucleoidExtrasEvents;
import xyz.nucleoid.extras.integrations.status.HasTickPerformanceLog;
import xyz.nucleoid.extras.integrations.status.ServerLifecycleIntegration;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements HasTickPerformanceLog {
    @Unique
    private final MultiValueDebugSampleLogImpl extras$tickPerformanceLog = new MultiValueDebugSampleLogImpl(1);

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
            method = "tick",
            at = @At("RETURN")
    )
    private void onEndTickIncludingPaused(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        NucleoidExtrasEvents.END_SERVER_TICK.invoker().onEndTick((MinecraftServer) (Object) this);
    }

    @Inject(
            method = "pushTickLog",
            at = @At(value = "HEAD")
    )
    public void pushTickPerformanceLog(long tickStartTime, CallbackInfo ci) {
        this.extras$tickPerformanceLog.push(Util.getMeasuringTimeNano() - tickStartTime);
    }

    @Override
    public MultiValueDebugSampleLog getTickPerformanceLog() {
        return this.extras$tickPerformanceLog;
    }
}
