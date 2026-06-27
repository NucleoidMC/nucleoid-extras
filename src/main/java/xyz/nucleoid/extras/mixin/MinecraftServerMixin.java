package xyz.nucleoid.extras.mixin;

import net.minecraft.CrashReport;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleStorage;
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
    private final LocalSampleLogger extras$tickPerformanceLog = new LocalSampleLogger(1);

    @ModifyArg(
            method = "runServer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerCrash(Lnet/minecraft/CrashReport;)V")
    )
    private CrashReport extras$onServerCrash(CrashReport report) {
        if (report != null) {
            ExtrasErrorReporter.onServerCrash(report);
            ServerLifecycleIntegration.setCrashed();
        }
        return report;
    }

    @Inject(
            method = "tickServer",
            at = @At("RETURN")
    )
    private void onEndTickIncludingPaused(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        NucleoidExtrasEvents.END_SERVER_TICK.invoker().onEndTick((MinecraftServer) (Object) this);
    }

    @Inject(
            method = "logTickMethodTime",
            at = @At(value = "HEAD")
    )
    public void pushTickPerformanceLog(long tickStartTime, CallbackInfo ci) {
        this.extras$tickPerformanceLog.logSample(Util.getNanos() - tickStartTime);
    }

    @Override
    public SampleStorage getTickPerformanceLog() {
        return this.extras$tickPerformanceLog;
    }
}
