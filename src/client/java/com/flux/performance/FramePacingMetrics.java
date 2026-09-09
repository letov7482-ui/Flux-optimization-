package com.flux.performance;

/**
 * Utility class for calculating frame pacing metrics.
 *
 * This class does not collect frame data itself.
 * It only converts frame-time statistics into useful metrics.
 */
public final class FramePacingMetrics {

    private FramePacingMetrics() {
        // Utility class
    }

    /**
     * Calculates a frame pacing score from 0 to 100.
     *
     * A lower relative deviation between frame times
     * results in a higher score.
     *
     * @param averageFrameTime average frame time in nanoseconds
     * @param standardDeviation frame-time standard deviation in nanoseconds
     * @return pacing score from 0.0 to 100.0
     */
    public static double calculatePacingScore(
            double averageFrameTime,
            double standardDeviation
    ) {
        if (averageFrameTime <= 0.0) {
            return 0.0;
        }

        if (standardDeviation < 0.0) {
            standardDeviation = 0.0;
        }

        double relativeDeviation =
                standardDeviation / averageFrameTime;

        double score = 100.0 - (relativeDeviation * 100.0);

        return clamp(score, 0.0, 100.0);
    }

    /**
     * Converts frame time in nanoseconds to milliseconds.
     */
    public static double nanosToMillis(double nanoseconds) {
        return nanoseconds / 1_000_000.0;
    }

    /**
     * Converts frame time in nanoseconds to approximate FPS.
     */
    public static int frameTimeToFps(double frameTimeNanos) {
        if (frameTimeNanos <= 0.0) {
            return 0;
        }

        return (int) Math.round(
                1_000_000_000.0 / frameTimeNanos
        );
    }

    /**
     * Determines whether a frame is considered a spike.
     *
     * @param frameTime current frame time in nanoseconds
     * @param averageFrameTime average frame time in nanoseconds
     * @param multiplier spike threshold multiplier
     */
    public static boolean isSpike(
            double frameTime,
            double averageFrameTime,
            double multiplier
    ) {
        if (frameTime <= 0.0 || averageFrameTime <= 0.0) {
            return false;
        }

        if (multiplier <= 1.0) {
            multiplier = 1.5;
        }

        return frameTime > averageFrameTime * multiplier;
    }

    /**
     * Returns how much slower a frame was compared
     * to the current average.
     *
     * Example:
     * average = 7 ms
     * current = 14 ms
     *
     * result = 2.0
     */
    public static double getFrameTimeRatio(
            double frameTime,
            double averageFrameTime
    ) {
        if (frameTime <= 0.0 || averageFrameTime <= 0.0) {
            return 0.0;
        }

        return frameTime / averageFrameTime;
    }

    /**
     * Clamps a value to the specified range.
     */
    private static double clamp(
            double value,
            double minimum,
            double maximum
    ) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
