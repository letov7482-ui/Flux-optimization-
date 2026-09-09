package com.flux.gui;

import com.flux.FluxClient;
import com.flux.performance.FramePacingEngine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class FluxScreen extends Screen {

    private static final int ACCENT = 0xFF8B5CF6;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFF777B84;
    private static final int PANEL = 0xF20D0F12;

    private final Screen parent;

    public FluxScreen(Screen parent) {
        super(Component.literal("Flux"));
        this.parent = parent;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        renderBackground(graphics);

        FramePacingEngine engine = FluxClient.getFramePacing();

        int centerX = width / 2;
        int centerY = height / 2;

        int panelWidth = 720;
        int panelHeight = 420;

        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;

        // Main panel
        graphics.fill(
                left,
                top,
                left + panelWidth,
                top + panelHeight,
                PANEL
        );

        // Accent
        graphics.fill(
                left,
                top,
                left + 3,
                top + panelHeight,
                ACCENT
        );

        // Header
        graphics.drawCenteredString(
                font,
                Component.literal("F L U X"),
                centerX,
                top + 28,
                TEXT
        );

        graphics.drawCenteredString(
                font,
                Component.literal("PERFORMANCE ENGINE"),
                centerX,
                top + 48,
                0xFF8F9299
        );

        // Section
        graphics.drawString(
                font,
                Component.literal("FRAME PACING"),
                left + 32,
                top + 88,
                TEXT
        );

        graphics.drawString(
                font,
                Component.literal(
                        "Real-time frame delivery analysis"
                ),
                left + 32,
                top + 108,
                MUTED
        );

        // Metrics
        drawMetric(
                graphics,
                "FPS",
                String.valueOf(engine.getCurrentFps()),
                left + 32,
                top + 145
        );

        drawMetric(
                graphics,
                "FRAME TIME",
                formatMs(engine.getAverageFrameTimeMs()),
                left + 155,
                top + 145
        );

        drawMetric(
                graphics,
                "STABILITY",
                formatPercent(engine.getStability()),
                left + 310,
                top + 145
        );

        drawMetric(
                graphics,
                "SPIKES",
                String.valueOf(engine.getSpikeCount()),
                left + 455,
                top + 145
        );

        // Graph
        drawGraph(
                graphics,
                engine,
                left + 32,
                top + 205,
                panelWidth - 64,
                145
        );

        // Bottom status
        String status;

        if (engine.getSampleCount() < 10) {
            status = "CALIBRATING";
        } else if (engine.hasRecentSpike()) {
            status = "FRAME SPIKE DETECTED";
        } else {
            status = "STABLE";
        }

        graphics.drawString(
                font,
                Component.literal(status),
                left + 32,
                top + 370,
                engine.hasRecentSpike()
                        ? 0xFFFFB4B4
                        : ACCENT
        );

        graphics.drawString(
                font,
                Component.literal(
                        engine.getSampleCount() + " / 120 samples"
                ),
                left + panelWidth - 145,
                top + 370,
                MUTED
        );

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawMetric(
            GuiGraphics graphics,
            String label,
            String value,
            int x,
            int y
    ) {
        graphics.drawString(
                font,
                Component.literal(label),
                x,
                y,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(value),
                x,
                y + 20,
                TEXT
        );
    }

    private void drawGraph(
            GuiGraphics graphics,
            FramePacingEngine engine,
            int x,
            int y,
            int graphWidth,
            int graphHeight
    ) {
        // Graph background
        graphics.fill(
                x,
                y,
                x + graphWidth,
                y + graphHeight,
                0xAA08090C
        );

        // Grid
        int quarter = graphHeight / 4;

        for (int i = 1; i < 4; i++) {
            int lineY = y + quarter * i;

            graphics.fill(
                    x,
                    lineY,
                    x + graphWidth,
                    lineY + 1,
                    0x332F333A
            );
        }

        long[] samples = engine.getFrameTimes();

        if (samples.length < 2) {
            graphics.drawString(
                    font,
                    Component.literal("Collecting frame data..."),
                    x + 12,
                    y + graphHeight / 2 - 4,
                    MUTED
            );

            return;
        }

        double maxMs = 0.0;

        for (long sample : samples) {
            double ms = sample / 1_000_000.0;

            if (ms > maxMs) {
                maxMs = ms;
            }
        }

        // Keep the graph readable.
        maxMs = Math.max(maxMs, 16.0);

        // Prevent one huge spike from destroying the graph scale.
        maxMs = Math.min(maxMs, 100.0);

        for (int i = 1; i < samples.length; i++) {
            double previousMs =
                    samples[i - 1] / 1_000_000.0;

            double currentMs =
                    samples[i] / 1_000_000.0;

            previousMs = Math.min(previousMs, maxMs);
            currentMs = Math.min(currentMs, maxMs);

            int previousX =
                    x + (int) ((i - 1) *
                            (graphWidth - 1.0) /
                            (samples.length - 1));

            int currentX =
                    x + (int) (i *
                            (graphWidth - 1.0) /
                            (samples.length - 1));

            int previousY =
                    y + graphHeight -
                            (int) ((previousMs / maxMs) *
                                    (graphHeight - 1));

            int currentY =
                    y + graphHeight -
                            (int) ((currentMs / maxMs) *
                                    (graphHeight - 1));

            drawLine(
                    graphics,
                    previousX,
                    previousY,
                    currentX,
                    currentY,
                    ACCENT
            );
        }

        // Scale labels
        graphics.drawString(
                font,
                Component.literal(
                        String.format(
                                Locale.ROOT,
                                "%.0f ms",
                                maxMs
                        )
                ),
                x + 6,
                y + 5,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal("0 ms"),
                x + 6,
                y + graphHeight - 12,
                MUTED
        );
    }

    private void drawLine(
            GuiGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    ) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(
                Math.abs(dx),
                Math.abs(dy)
        );

        if (steps == 0) {
            graphics.fill(
                    x1,
                    y1,
                    x1 + 1,
                    y1 + 1,
                    color
            );
            return;
        }

        for (int i = 0; i <= steps; i++) {
            int x = x1 + dx * i / steps;
            int y = y1 + dy * i / steps;

            graphics.fill(
                    x,
                    y,
                    x + 2,
                    y + 2,
                    color
            );
        }
    }

    private String formatMs(double value) {
        if (value <= 0.0) {
            return "-- ms";
        }

        return String.format(
                Locale.ROOT,
                "%.2f ms",
                value
        );
    }

    private String formatPercent(double value) {
        if (value <= 0.0) {
            return "--%";
        }

        return String.format(
                Locale.ROOT,
                "%.0f%%",
                value
        );
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
