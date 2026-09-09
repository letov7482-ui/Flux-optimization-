package com.flux.performance;

public final class RenderOptimizationEngine {

    private boolean enabled = true;
    private boolean adaptive;

    private long renderStartTime;
    private long lastRenderTime;

    private double averageRenderTimeMs;
    private double peakRenderTimeMs;

    private int samples;

    public void beginRender() {
        if (!enabled) {
            return;
        }

        renderStartTime = System.nanoTime();
    }

    public void endRender() {
        if (!enabled || renderStartTime == 0L) {
            return;
        }

        long elapsed = System.nanoTime() - renderStartTime;

        if (elapsed <= 0L) {
            return;
        }

        lastRenderTime = elapsed;

        double milliseconds = elapsed / 1_000_000.0;

        if (samples == 0) {
            averageRenderTimeMs = milliseconds;
        } else {
            averageRenderTimeMs =
                    averageRenderTimeMs * 0.90
                            + milliseconds * 0.10;
        }

        if (milliseconds > peakRenderTimeMs) {
            peakRenderTimeMs = milliseconds;
        }

        samples++;
    }

    public void update(OptimizationEngine optimization) {
        if (!enabled || optimization == null) {
            adaptive = false;
            return;
        }

        OptimizationEngine.State state =
                optimization.getState();

        adaptive =
                state == OptimizationEngine.State.LIGHT
                        || state == OptimizationEngine.State.ACTIVE
                        || state == OptimizationEngine.State.CRITICAL;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled) {
            adaptive = false;
            renderStartTime = 0L;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdaptive() {
        return adaptive;
    }

    public double getLastRenderTimeMs() {
        return lastRenderTime / 1_000_000.0;
    }

    public double getAverageRenderTimeMs() {
        return averageRenderTimeMs;
    }

    public double getPeakRenderTimeMs() {
        return peakRenderTimeMs;
    }

    public int getSamples() {
        return samples;
    }

    public String getStateText() {
        if (!enabled) {
            return "Disabled";
        }

        if (adaptive) {
            return "Adaptive";
        }

        return "Normal";
    }

    public void reset() {
        renderStartTime = 0L;
        lastRenderTime = 0L;

        averageRenderTimeMs = 0.0;
        peakRenderTimeMs = 0.0;

        samples = 0;

        adaptive = false;
    }
}
