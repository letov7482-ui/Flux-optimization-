package com.flux.performance;

public final class OptimizationEngine {

    public enum State {
        IDLE,
        LIGHT,
        ACTIVE,
        CRITICAL
    }

    private State state = State.IDLE;

    private boolean framePacingAssist;
    private boolean adaptiveMode;

    private int optimizationChanges;

    public void update(AdaptivePerformanceEngine adaptive) {
        if (adaptive == null) {
            reset();
            return;
        }

        AdaptivePerformanceEngine.Mode mode = adaptive.getMode();

        if (mode == AdaptivePerformanceEngine.Mode.OFF) {
            reset();
            return;
        }

        int level = adaptive.getOptimizationLevel();

        switch (level) {
            case 0 -> applyIdle();
            case 1 -> applyLight();
            default -> applyCritical();
        }
    }

    private void applyIdle() {
        setState(State.IDLE);

        framePacingAssist = false;
        adaptiveMode = true;
    }

    private void applyLight() {
        setState(State.LIGHT);

        framePacingAssist = true;
        adaptiveMode = true;
    }

    private void applyCritical() {
        setState(State.CRITICAL);

        framePacingAssist = true;
        adaptiveMode = true;
    }

    private void setState(State newState) {
        if (state != newState) {
            state = newState;
            optimizationChanges++;
        }
    }

    public State getState() {
        return state;
    }

    public boolean isFramePacingAssistEnabled() {
        return framePacingAssist;
    }

    public boolean isAdaptiveModeEnabled() {
        return adaptiveMode;
    }

    public int getOptimizationChanges() {
        return optimizationChanges;
    }

    public String getStateText() {
        return switch (state) {
            case IDLE -> "Idle";
            case LIGHT -> "Light optimization";
            case ACTIVE -> "Active";
            case CRITICAL -> "Critical optimization";
        };
    }

    public String getDescription() {
        return switch (state) {
            case IDLE ->
                    "Flux is monitoring performance.";
            case LIGHT ->
                    "Flux detected frame-time pressure.";
            case ACTIVE ->
                    "Flux is actively optimizing performance.";
            case CRITICAL ->
                    "Flux detected severe performance pressure.";
        };
    }

    public void reset() {
        state = State.IDLE;

        framePacingAssist = false;
        adaptiveMode = false;

        optimizationChanges = 0;
    }
}
