package com.flux.performance;

public final class AdaptivePerformanceEngine {

    public enum Mode {
        OFF,
        ADAPTIVE,
        AGGRESSIVE
    }

    private static final int EVALUATION_INTERVAL = 20;

    private static final double LOW_STABILITY = 70.0;
    private static final double CRITICAL_STABILITY = 50.0;

    private Mode mode = Mode.ADAPTIVE;

    private int ticksUntilEvaluation;

    private boolean adaptiveActive;
    private boolean performancePressure;
    private boolean criticalPressure;

    private int optimizationLevel;

    private double lastFps;
    private double lastFrameTime;
    private double lastStability;

    public void tick(FramePacingEngine framePacing) {
        if (mode == Mode.OFF) {
            adaptiveActive = false;
            performancePressure = false;
            criticalPressure = false;
            optimizationLevel = 0;
            return;
        }

        if (--ticksUntilEvaluation > 0) {
            return;
        }

        ticksUntilEvaluation = EVALUATION_INTERVAL;

        evaluate(framePacing);
    }

    private void evaluate(FramePacingEngine framePacing) {
        lastFps = framePacing.getCurrentFps();
        lastFrameTime = framePacing.getAverageFrameTimeMs();
        lastStability = framePacing.getStability();

        if (framePacing.getSampleCount() < 20) {
            adaptiveActive = false;
            performancePressure = false;
            criticalPressure = false;
            optimizationLevel = 0;
            return;
        }

        performancePressure =
                lastStability < LOW_STABILITY
                        || lastFrameTime > 25.0;

        criticalPressure =
                lastStability < CRITICAL_STABILITY
                        || lastFrameTime > 40.0;

        if (!performancePressure) {
            adaptiveActive = false;
            optimizationLevel = 0;
            return;
        }

        adaptiveActive = true;

        if (mode == Mode.AGGRESSIVE && criticalPressure) {
            optimizationLevel = 2;
        } else {
            optimizationLevel = 1;
        }
    }

    public void setMode(Mode mode) {
        if (mode == null) {
            return;
        }

        this.mode = mode;

        if (mode == Mode.OFF) {
            adaptiveActive = false;
            performancePressure = false;
            criticalPressure = false;
            optimizationLevel = 0;
        }
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isActive() {
        return adaptiveActive;
    }

    public boolean hasPerformancePressure() {
        return performancePressure;
    }

    public boolean hasCriticalPressure() {
        return criticalPressure;
    }

    public int getOptimizationLevel() {
        return optimizationLevel;
    }

    public double getLastFps() {
        return lastFps;
    }

    public double getLastFrameTime() {
        return lastFrameTime;
    }

    public double getLastStability() {
        return lastStability;
    }

    public String getStateText() {
        if (mode == Mode.OFF) {
            return "Disabled";
        }

        if (criticalPressure) {
            return "Critical load";
        }

        if (performancePressure) {
            return "Optimizing";
        }

        return "Monitoring";
    }

    public String getDescription() {
        if (mode == Mode.OFF) {
            return "Adaptive optimization is disabled.";
        }

        if (criticalPressure) {
            return "High frame-time pressure detected.";
        }

        if (performancePressure) {
            return "Flux is responding to unstable frame delivery.";
        }

        return "Flux is monitoring performance.";
    }

    public void reset() {
        ticksUntilEvaluation = 0;

        adaptiveActive = false;
        performancePressure = false;
        criticalPressure = false;

        optimizationLevel = 0;

        lastFps = 0.0;
        lastFrameTime = 0.0;
        lastStability = 0.0;
    }
}
