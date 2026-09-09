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
    private static final int TEXT_SECONDARY = 0xFFE5E7EB;
    private static final int MUTED = 0xFF777B84;

    private static final int PANEL = 0xF20D0F12;
    private static final int PANEL_LIGHT = 0xAA15171C;
    private static final int PANEL_HOVER = 0xFF1B1D23;
    private static final int GRID = 0x332F333A;

    private static final int SIDEBAR_WIDTH = 150;

    private final Screen parent;

    private int selectedPage = 0;

    private final String[] pages = {
            "OVERVIEW",
            "PERFORMANCE",
            "FRAME PACING",
            "FLUX TUNER",
            "OPTIMIZATION"
    };

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

        int panelWidth = 760;
        int panelHeight = 440;

        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;

        // Main container
        graphics.fill(
                left,
                top,
                left + panelWidth,
                top + panelHeight,
                PANEL
        );

        // Flux accent
        graphics.fill(
                left,
                top,
                left + 3,
                top + panelHeight,
                ACCENT
        );

        // Sidebar
        drawSidebar(
                graphics,
                left,
                top,
                panelHeight,
                mouseX,
                mouseY
        );

        // Header
        drawHeader(
                graphics,
                left,
                top,
                panelWidth
        );

        // Page content
        int contentLeft = left + SIDEBAR_WIDTH + 24;
        int contentTop = top + 72;

        switch (selectedPage) {
            case 0 -> drawOverview(
                    graphics,
                    engine,
                    contentLeft,
                    contentTop,
                    left + panelWidth - 24,
                    top + panelHeight - 24
            );

            case 1 -> drawPerformance(
                    graphics,
                    engine,
                    contentLeft,
                    contentTop,
                    left + panelWidth - 24
            );

            case 2 -> drawFramePacing(
                    graphics,
                    engine,
                    contentLeft,
                    contentTop,
                    left + panelWidth - 24
            );

            case 3 -> drawTunerPlaceholder(
                    graphics,
                    contentLeft,
                    contentTop,
                    left + panelWidth - 24
            );

            case 4 -> drawOptimizationPlaceholder(
                    graphics,
                    contentLeft,
                    contentTop,
                    left + panelWidth - 24
            );
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawHeader(
            GuiGraphics graphics,
            int left,
            int top,
            int panelWidth
    ) {
        graphics.drawString(
                font,
                Component.literal("F L U X"),
                left + 24,
                top + 18,
                TEXT
        );

        graphics.drawString(
                font,
                Component.literal("PERFORMANCE ENGINE"),
                left + 24,
                top + 36,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal("1.0.0"),
                left + panelWidth - 50,
                top + 23,
                MUTED
        );
    }

    private void drawSidebar(
            GuiGraphics graphics,
            int left,
            int top,
            int panelHeight,
            int mouseX,
            int mouseY
    ) {
        int sidebarLeft = left + 12;
        int sidebarTop = top + 76;

        for (int i = 0; i < pages.length; i++) {

            int itemTop = sidebarTop + i * 42;

            boolean selected = selectedPage == i;

            boolean hovered =
                    mouseX >= sidebarLeft
                            && mouseX <= sidebarLeft + SIDEBAR_WIDTH - 20
                            && mouseY >= itemTop
                            && mouseY <= itemTop + 32;

            // Hover background
            if (hovered && !selected) {
                graphics.fill(
                        sidebarLeft,
                        itemTop,
                        sidebarLeft + SIDEBAR_WIDTH - 20,
                        itemTop + 32,
                        PANEL_HOVER
                );
            }

            // Selected background
            if (selected) {
                graphics.fill(
                        sidebarLeft,
                        itemTop,
                        sidebarLeft + SIDEBAR_WIDTH - 20,
                        itemTop + 32,
                        PANEL_LIGHT
                );

                graphics.fill(
                        sidebarLeft,
                        itemTop,
                        sidebarLeft + 2,
                        itemTop + 32,
                        ACCENT
                );
            }

            // Indicator
            graphics.drawString(
                    font,
                    Component.literal(selected ? "●" : "○"),
                    sidebarLeft + 10,
                    itemTop + 9,
                    selected ? ACCENT : MUTED
            );

            // Page name
            graphics.drawString(
                    font,
                    Component.literal(pages[i]),
                    sidebarLeft + 28,
                    itemTop + 9,
                    selected ? TEXT : MUTED
            );
        }

        // Sidebar footer
        graphics.drawString(
                font,
                Component.literal("FLUX ENGINE"),
                sidebarLeft + 8,
                top + panelHeight - 30,
                MUTED
        );
    }

    private void drawOverview(
            GuiGraphics graphics,
            FramePacingEngine engine,
            int left,
            int top,
            int right,
            int bottom
    ) {
        drawPageTitle(
                graphics,
                "OVERVIEW",
                "Real-time performance overview",
                left,
                top
        );

        int cardTop = top + 48;
        int cardHeight = 62;
        int gap = 8;

        int totalWidth = right - left;
        int cardWidth = (totalWidth - gap * 2) / 3;

        drawStatCard(
                graphics,
                "FPS",
                String.valueOf(engine.getCurrentFps()),
                left,
                cardTop,
                cardWidth,
                cardHeight
        );

        drawStatCard(
                graphics,
                "FRAME TIME",
                formatMs(engine.getAverageFrameTimeMs()),
                left + cardWidth + gap,
                cardTop,
                cardWidth,
                cardHeight
        );

        drawStatCard(
                graphics,
                "STABILITY",
                formatPercent(engine.getStability()),
                left + (cardWidth + gap) * 2,
                cardTop,
                cardWidth,
                cardHeight
        );

        int graphTop = cardTop + cardHeight + 18;

        graphics.drawString(
                font,
                Component.literal("FRAME TIME"),
                left,
                graphTop,
                TEXT
        );

        graphics.drawString(
                font,
                Component.literal("120-frame history"),
                right - 100,
                graphTop,
                MUTED
        );

        drawGraph(
                graphics,
                engine,
                left,
                graphTop + 18,
                right - left,
                bottom - graphTop - 48
        );

        String status;

        if (engine.getSampleCount() < 10) {
            status = "CALIBRATING";
        } else if (engine.hasRecentSpike()) {
            status = "FRAME SPIKE DETECTED";
        } else {
            status = "ENGINE STABLE";
        }

        graphics.drawString(
                font,
                Component.literal("● " + status),
                left,
                bottom - 12,
                engine.hasRecentSpike()
                        ? 0xFFFFB4B4
                        : ACCENT
        );
    }

    private void drawPerformance(
            GuiGraphics graphics,
            FramePacingEngine engine,
            int left,
            int top,
            int right
    ) {
        drawPageTitle(
                graphics,
                "PERFORMANCE",
                "Current engine statistics",
                left,
                top
        );

        int cardTop = top + 50;
        int cardWidth = (right - left - 8) / 2;

        drawLargeMetric(
                graphics,
                "CURRENT FPS",
                String.valueOf(engine.getCurrentFps()),
                left,
                cardTop,
                cardWidth,
                72
        );

        drawLargeMetric(
                graphics,
                "AVERAGE FRAME TIME",
                formatMs(engine.getAverageFrameTimeMs()),
                left + cardWidth + 8,
                cardTop,
                cardWidth,
                72
        );

        drawLargeMetric(
                graphics,
                "FRAME TIME DEVIATION",
                formatMs(engine.getStandardDeviationMs()),
                left,
                cardTop + 82,
                cardWidth,
                72
        );

        drawLargeMetric(
                graphics,
                "FRAME SPIKES",
                String.valueOf(engine.getSpikeCount()),
                left + cardWidth + 8,
                cardTop + 82,
                cardWidth,
                72
        );

        int infoTop = cardTop + 175;

        graphics.drawString(
                font,
                Component.literal("ENGINE"),
                left,
                infoTop,
                TEXT
        );

        graphics.drawString(
                font,
                Component.literal(
                        "Flux continuously monitors frame delivery"
                ),
                left,
                infoTop + 22,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(
                        "to identify instability and frame-time spikes."
                ),
                left,
                infoTop + 38,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(
                        engine.getSampleCount() + " / 120 samples collected"
                ),
                left,
                infoTop + 70,
                TEXT_SECONDARY
        );
    }

    private void drawFramePacing(
            GuiGraphics graphics,
            FramePacingEngine engine,
            int left,
            int top,
            int right
    ) {
        drawPageTitle(
                graphics,
                "FRAME PACING",
                "Frame delivery consistency analysis",
                left,
                top
        );

        int cardTop = top + 50;

        drawLargeMetric(
                graphics,
                "PACING SCORE",
                formatPercent(engine.getPacingScore()),
                left,
                cardTop,
                right - left,
                72
        );

        int graphTop = cardTop + 92;

        graphics.drawString(
                font,
                Component.literal("FRAME TIME HISTORY"),
                left,
                graphTop,
                TEXT
        );

        drawGraph(
                graphics,
                engine,
                left,
                graphTop + 20,
                right - left,
                150
        );

        graphics.drawString(
                font,
                Component.literal("FRAME SPIKES"),
                left,
                graphTop + 190,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(
                        String.valueOf(engine.getSpikeCount())
                ),
                left,
                graphTop + 208,
                TEXT_SECONDARY
        );

        graphics.drawString(
                font,
                Component.literal("STANDARD DEVIATION"),
                left + 130,
                graphTop + 190,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(
                        formatMs(engine.getStandardDeviationMs())
                ),
                left + 130,
                graphTop + 208,
                TEXT_SECONDARY
        );
    }

    private void drawTunerPlaceholder(
            GuiGraphics graphics,
            int left,
            int top,
            int right
    ) {
        drawPageTitle(
                graphics,
                "FLUX TUNER",
                "Intelligent Sodium configuration",
                left,
                top
        );

        drawLargeMetric(
                graphics,
                "STATUS",
                "COMING SOON",
                left,
                top + 52,
                right - left,
                72
        );

        graphics.drawString(
                font,
                Component.literal("SODIUM"),
                left,
                top + 150,
                TEXT
        );

        graphics.drawString(
                font,
                Component.literal(
                        "Flux will detect supported Sodium versions"
                ),
                left,
                top + 174,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(
                        "and provide optimized profiles with Restore."
                ),
                left,
                top + 190,
                MUTED
        );
    }

    private void drawOptimizationPlaceholder(
            GuiGraphics graphics,
            int left,
            int top,
            int right
    ) {
        drawPageTitle(
                graphics,
                "OPTIMIZATION",
                "Flux performance systems",
                left,
                top
        );

        drawToggleRow(
                graphics,
                "Frame Pacing",
                "Consistent frame delivery",
                true,
                left,
                top + 55,
                right
        );

        drawToggleRow(
                graphics,
                "Smart Tick",
                "Safe client-side workload optimization",
                false,
                left,
                top + 112,
                right
        );

        drawToggleRow(
                graphics,
                "Memory Optimization",
                "Reduce unnecessary client overhead",
                false,
                left,
                top + 169,
                right
        );
    }

    private void drawPageTitle(
            GuiGraphics graphics,
            String title,
            String subtitle,
            int x,
            int y
    ) {
        graphics.drawString(
                font,
                Component.literal(title),
                x,
                y,
                TEXT
        );

        graphics.drawString(
                font,
                Component.literal(subtitle),
                x,
                y + 18,
                MUTED
        );
    }

    private void drawStatCard(
            GuiGraphics graphics,
            String label,
            String value,
            int x,
            int y,
            int width,
            int height
    ) {
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                PANEL_LIGHT
        );

        graphics.drawString(
                font,
                Component.literal(label),
                x + 12,
                y + 10,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(value),
                x + 12,
                y + 30,
                TEXT
        );
    }

    private void drawLargeMetric(
            GuiGraphics graphics,
            String label,
            String value,
            int x,
            int y,
            int width,
            int height
    ) {
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                PANEL_LIGHT
        );

        graphics.drawString(
                font,
                Component.literal(label),
                x + 14,
                y + 12,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(value),
                x + 14,
                y + 34,
                TEXT
        );
    }

    private void drawToggleRow(
            GuiGraphics graphics,
            String title,
            String description,
            boolean enabled,
            int x,
            int y,
            int right
    ) {
        graphics.fill(
                x,
                y,
                right,
                y + 48,
                PANEL_LIGHT
        );

        graphics.drawString(
                font,
                Component.literal(title),
                x + 12,
                y + 9,
                TEXT_SECONDARY
        );

        graphics.drawString(
                font,
                Component.literal(description),
                x + 12,
                y + 26,
                MUTED
        );

        graphics.drawString(
                font,
                Component.literal(enabled ? "ON" : "OFF"),
                right - 32,
                y + 17,
                enabled ? ACCENT : MUTED
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
        if (graphWidth <= 0 || graphHeight <= 0) {
            return;
        }

        graphics.fill(
                x,
                y,
                x + graphWidth,
                y + graphHeight,
                0xAA08090C
        );

        int quarter = graphHeight / 4;

        for (int i = 1; i < 4; i++) {
            int lineY = y + quarter * i;

            graphics.fill(
                    x,
                    lineY,
                    x + graphWidth,
                    lineY + 1,
                    GRID
            );
        }

        long[] samples = engine.getFrameTimes();

        if (samples.length < 2) {
            graphics.drawString(
                    font,
                    Component.literal("Collecting frame data..."),
                    x + 12,
                    y + graphHeight / 2 - 4,
                                x + 12,
                    y + graphHeight / 2 - 4,
                    Component.literal("Collecting frame data..."),
                    MUTED
            );
            return;
        }

        long maxFrameTime = 0L;

        for (long sample : samples) {
            if (sample > maxFrameTime) {
                maxFrameTime = sample;
            }
        }

        if (maxFrameTime <= 0L) {
            return;
        }

        int pointCount = samples.length;

        for (int i = 1; i < pointCount; i++) {
            long previous = samples[i - 1];
            long current = samples[i];

            int previousX =
                    x + (int) ((long) (graphWidth - 1) * (i - 1) / (pointCount - 1));

            int currentX =
                    x + (int) ((long) (graphWidth - 1) * i / (pointCount - 1));

            int previousY =
                    y + graphHeight - 1
                            - (int) ((double) previous / maxFrameTime * (graphHeight - 1));

            int currentY =
                    y + graphHeight - 1
                            - (int) ((double) current / maxFrameTime * (graphHeight - 1));

            previousY = Math.max(y, Math.min(y + graphHeight - 1, previousY));
            currentY = Math.max(y, Math.min(y + graphHeight - 1, currentY));

            drawLine(
                    graphics,
                    previousX,
                    previousY,
                    currentX,
                    currentY,
                    ACCENT
            );
        }

        long average = 0L;

        for (long sample : samples) {
            average += sample;
        }

        average /= samples.length;

        int averageY =
                y + graphHeight - 1
                        - (int) ((double) average / maxFrameTime * (graphHeight - 1));

        averageY = Math.max(
                y,
                Math.min(y + graphHeight - 1, averageY)
        );

        graphics.fill(
                x,
                averageY,
                x + graphWidth,
                averageY + 1,
                0x555F636D
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
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int steps = Math.max(dx, dy);

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
            float progress = (float) i / steps;

            int x =
                    Math.round(x1 + (x2 - x1) * progress);

            int y =
                    Math.round(y1 + (y2 - y1) * progress);

            graphics.fill(
                    x,
                    y,
                    x + 2,
                    y + 2,
                    color
            );
        }
    }

    private String formatFrameTime(double milliseconds) {
        if (milliseconds <= 0.0) {
            return "--";
        }

        return String.format(
                java.util.Locale.ROOT,
                "%.2f ms",
                milliseconds
        );
    }

    private String formatFps(int fps) {
        if (fps <= 0) {
            return "--";
        }

        return fps + " FPS";
    }

    private String formatPercentage(double value) {
        if (value <= 0.0) {
            return "--";
        }

        return String.format(
                java.util.Locale.ROOT,
                "%.0f%%",
                value
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int screenWidth = this.width;
        int screenHeight = this.height;

        int panelWidth = 760;
        int panelHeight = 440;

        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = (screenHeight - panelHeight) / 2;

        int sidebarX = panelX;
        int sidebarY = panelY;

        if (mouseX >= sidebarX
                && mouseX < sidebarX + SIDEBAR_WIDTH
                && mouseY >= sidebarY
                && mouseY < sidebarY + panelHeight) {

            int itemHeight = 54;
            int startY = sidebarY + 82;

            for (int i = 0; i < PAGES.length; i++) {
                int itemY = startY + i * itemHeight;

                if (mouseY >= itemY
                        && mouseY < itemY + itemHeight) {

                    selectedPage = i;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(previousScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
            }
