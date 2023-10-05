package xyz.nucleoid.extras.integrations.status;

import net.minecraft.util.profiler.PerformanceLog;

public interface HasTickPerformanceLog {
    public PerformanceLog getTickPerformanceLog();
}
