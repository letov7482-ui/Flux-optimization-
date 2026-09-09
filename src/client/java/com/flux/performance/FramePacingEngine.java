package com.flux.performance;

import net.minecraft.client.Minecraft;

public final class FramePacingEngine {

    private static final int SAMPLE_COUNT = 120;

    private final long[] frameTimes = new long[SAMPLE_COUNT];

    private int sampleIndex;
    private int sampleCount;

    private long lastFrameTime;

    private double averageFrameTime;
    private double stability;

    public void beginFrame() {
        long now = System.nanoTime();

        if (lastFrameTime != 0L) {
            long frameTime = now - lastFrameTime;

            recordFrameTime(frameTime);
        }

        lastFrameTime = now;
    }

    private void recordFrameTime(long frameTime) {
        frameTimes[sampleIndex] = frameTime;

        sampleIndex = (sampleIndex + 1) % SAMPLE_COUNT;

        if (sampleCount < SAMPLE_COUNT) {
            sampleCount++;
        }

        updateMetrics();
    }

    private void updateMetrics() {
        if (sampleCount == 0) {
            return;
        }

        double total = 0.0;

        for (int i = 0; i < sampleCount; i++) {
            total += frameTimes[i];
        }

        averageFrameTime = total / sampleCount;

        double variance = 0.0;

        for (int i = 0; i < sampleCount; i++) {
            double difference = frameTimes[i] - averageFrameTime;
            variance += difference * difference;
        }

        variance /= sampleCount;

        double standardDeviation = Math.sqrt(variance);

        /*
         * Lower frame-time deviation means better frame pacing.
         *
         * This is intentionally a normalized metric rather than
         * pretending to be a perfect benchmark score.
         */
        stability = 100.0 - Math.min(
                100.0,
                (standardDeviation / Math.max(averageFrameTime, 0.1)) * 100.0
        );
    }

    public double getAverageFrameTimeMs() {
        return averageFrameTime / 1_000_000.0;
    }

    public double getStability() {
        return stability;
    }

    public int getCurrentFps() {
        if (averageFrameTime <= 0.0) {
            return 0;
        }

        return (int) Math.round(
                1_000_000_000.0 / averageFrameTime
        );
    }

    public void reset() {
        sampleIndex = 0;
        sampleCount = 0;
        lastFrameTime = 0L;
        averageFrameTime = 0.0;
        stability = 0.0;

        for (int i = 0; i < frameTimes.length; i++) {
            frameTimes[i] = 0L;
        }
    }
}
