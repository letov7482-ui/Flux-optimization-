package com.flux.performance;

public final class FramePacingEngine {

    private static final int SAMPLE_COUNT = 120;
    private static final double SPIKE_MULTIPLIER = 1.75;

    private final long[] frameTimes = new long[SAMPLE_COUNT];

    private int sampleIndex;
    private int sampleCount;

    private long lastFrameTime;

    private double averageFrameTime;
    private double standardDeviation;
    private double stability;
    private double pacingScore;

    private int spikeCount;
    private long lastSpikeTime;

    public void beginFrame() {
        long now = System.nanoTime();

        if (lastFrameTime != 0L) {
            long frameTime = now - lastFrameTime;

            if (frameTime > 0L) {
                recordFrameTime(frameTime);
            }
        }

        lastFrameTime = now;
    }

    private void recordFrameTime(long frameTime) {
        double previousAverage = averageFrameTime;

        frameTimes[sampleIndex] = frameTime;

        sampleIndex = (sampleIndex + 1) % SAMPLE_COUNT;

        if (sampleCount < SAMPLE_COUNT) {
            sampleCount++;
        }

        updateMetrics();

        if (sampleCount >= 10
                && previousAverage > 0.0
                && FramePacingMetrics.isSpike(
                frameTime,
                previousAverage,
                SPIKE_MULTIPLIER
        )) {
            spikeCount++;
            lastSpikeTime = System.nanoTime();
        }
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
            double difference =
                    frameTimes[i] - averageFrameTime;

            variance += difference * difference;
        }

        variance /= sampleCount;

        standardDeviation = Math.sqrt(variance);

        stability = FramePacingMetrics.calculatePacingScore(
                averageFrameTime,
                standardDeviation
        );

        pacingScore = stability;
    }

    public int getCurrentFps() {
        return FramePacingMetrics.frameTimeToFps(
                averageFrameTime
        );
    }

    public double getAverageFrameTimeMs() {
        return FramePacingMetrics.nanosToMillis(
                averageFrameTime
        );
    }

    public double getStandardDeviationMs() {
        return FramePacingMetrics.nanosToMillis(
                standardDeviation
        );
    }

    public double getStability() {
        return stability;
    }

    public double getPacingScore() {
        return pacingScore;
    }

    public int getSpikeCount() {
        return spikeCount;
    }

    public boolean hasRecentSpike() {
        if (lastSpikeTime == 0L) {
            return false;
        }

        return System.nanoTime() - lastSpikeTime < 500_000_000L;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void reset() {
        sampleIndex = 0;
        sampleCount = 0;

        lastFrameTime = 0L;

        averageFrameTime = 0.0;
        standardDeviation = 0.0;
        stability = 0.0;
        pacingScore = 0.0;

        spikeCount = 0;
        lastSpikeTime = 0L;

        for (int i = 0; i < frameTimes.length; i++) {
            frameTimes[i] = 0L;
        }
    }
}
