package com.flux.performance;

public final class FramePacingEngine {

    private static final int SAMPLE_COUNT = 120;

    private final long[] frameTimes = new long[SAMPLE_COUNT];

    private int sampleIndex;
    private int sampleCount;

    private long lastFrameTime;

    private double averageFrameTime;
    private double stability;

    private int spikeCount;
    private long lastSpikeTime;

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

        stability = 100.0 - Math.min(
                100.0,
                (standardDeviation / Math.max(averageFrameTime, 0.1)) * 100.0
        );

        detectSpike(frameTimes[sampleIndex == 0
                ? SAMPLE_COUNT - 1
                : sampleIndex - 1]);
    }

    private void detectSpike(long frameTime) {
        if (sampleCount < 10 || averageFrameTime <= 0.0) {
            return;
        }

        /*
         * A frame is considered a spike when it takes
         * considerably longer than the current average.
         *
         * We deliberately use a conservative threshold:
         * Flux should detect noticeable stutters, not
         * treat every tiny variation as a problem.
         */
        double threshold = averageFrameTime * 1.75;

        if (frameTime > threshold) {
            spikeCount++;
            lastSpikeTime = System.nanoTime();
        }
    }

    public int getCurrentFps() {
        if (averageFrameTime <= 0.0) {
            return 0;
        }

        return (int) Math.round(
                1_000_000_000.0 / averageFrameTime
        );
    }

    public double getAverageFrameTimeMs() {
        return averageFrameTime / 1_000_000.0;
    }

    public double getStability() {
        return stability;
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
        stability = 0.0;

        spikeCount = 0;
        lastSpikeTime = 0L;

        for (int i = 0; i < frameTimes.length; i++) {
            frameTimes[i] = 0L;
        }
    }
}
