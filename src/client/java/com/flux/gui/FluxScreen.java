package com.flux.gui;

import com.flux.FluxClient;
import com.flux.performance.FramePacingEngine;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FluxScreen extends Screen {

    private final Screen parent;

    public FluxScreen(Screen parent) {
        super(Component.literal("Flux"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
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
                0xF20D0F12
        );

        // Accent line
        graphics.fill(
                left,
                top,
                left + 3,
                top + panelHeight,
                0xFF8B5CF6
        );

        // Header
        graphics.drawCenteredString(
                font,
                Component.literal("F L U X"),
                centerX,
                top + 28,
                0xFFFFFFFF
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
                Component.literal("PERFORMANCE"),
                left + 32,
                top + 90,
                0xFFFFFFFF
        );

        // Frame Pacing
        graphics.drawString(
                font,
                Component.literal("Frame Pacing"),
                left + 32,
                top + 125,
                0xFFE5E7EB
        );

        graphics.drawString(
                font,
                Component.literal(
                        "Keeps frame delivery consistent"
                ),
                left + 32,
                top + 142,
                0xFF777B84
        );

        graphics.drawString(
                font,
                Component.literal("ACTIVE"),
                left + panelWidth - 85,
                top + 125,
                0xFF8B5CF6
        );

        // Metrics
        drawMetric(
                graphics,
                "FPS",
                String.valueOf(engine.getCurrentFps()),
                left + 32,
                top + 200
        );

        drawMetric(
                graphics,
                "FRAME TIME",
                formatMs(engine.getAverageFrameTimeMs()),
                left + 170,
                top + 200
        );

        drawMetric(
                graphics,
                "STABILITY",
                formatPercent(engine.getStability()),
                left + 330,
                top + 200
        );

        drawMetric(
                graphics,
                "SPIKES",
                String.valueOf(engine.getSpikeCount()),
                left + 500,
                top + 200
        );

        // Status
        graphics.drawString(
                font,
                Component.literal("PACER"),
                left + 32,
                top + 280,
                0xFF777B84
        );

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
                top + 300,
                0xFFE5E7EB
        );

        // Sample information
        graphics.drawString(
                font,
                Component.literal("SAMPLES"),
                left + 32,
                top + 345,
                0xFF777B84
        );

        graphics.drawString(
                font,
                Component.literal(
                        engine.getSampleCount() + " / 120"
                ),
                left + 32,
                top + 365,
                0xFFFFFFFF
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
                0xFF777B84
        );

        graphics.drawString(
                font,
                Component.literal(value),
                x,
                y + 20,
                0xFFFFFFFF
        );
    }

    private String formatMs(double value) {
        if (value <= 0.0) {
            return "-- ms";
        }

        return String.format(
                java.util.Locale.ROOT,
                "%.2f ms",
                value
        );
    }

    private String formatPercent(double value) {
        if (value <= 0.0) {
            return "--%";
        }

        return String.format(
                java.util.Locale.ROOT,
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
