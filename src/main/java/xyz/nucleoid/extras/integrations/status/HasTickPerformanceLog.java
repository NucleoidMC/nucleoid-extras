package xyz.nucleoid.extras.integrations.status;

import net.minecraft.util.profiler.log.MultiValueDebugSampleLog;

public interface HasTickPerformanceLog {
    public MultiValueDebugSampleLog getTickPerformanceLog();
}
