package com.flux.gui;

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

        int centerX = width / 2;
        int centerY = height / 2;

        // Main panel
        int panelWidth = 720;
        int panelHeight = 420;

        int left = centerX - panelWidth / 2;
        int top = centerY - panelHeight / 2;

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

        // Logo
        graphics.drawCenteredString(
                font,
                Component.literal("F L U X"),
                centerX,
                top + 28,
                0xFFFFFFFF
        );

        // Subtitle
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
                Component.literal("Keeps frame delivery consistent"),
                left + 32,
                top + 142,
                0xFF777B84
        );

        // Status
        graphics.drawString(
                font,
                Component.literal("ACTIVE"),
                left + panelWidth - 85,
                top + 125,
                0xFF8B5CF6
        );

        // Metrics
        graphics.drawString(
                font,
                Component.literal("FPS"),
                left + 32,
                top + 200,
                0xFF777B84
        );

        graphics.drawString(
                font,
                Component.literal("--"),
                left + 32,
                top + 220,
                0xFFFFFFFF
        );

        graphics.drawString(
                font,
                Component.literal("FRAME TIME"),
                left + 150,
                top + 200,
                0xFF777B84
        );

        graphics.drawString(
                font,
                Component.literal("-- ms"),
                left + 150,
                top + 220,
                0xFFFFFFFF
        );

        graphics.drawString(
                font,
                Component.literal("STABILITY"),
                left + 300,
                top + 200,
                0xFF777B84
        );

        graphics.drawString(
                font,
                Component.literal("--%"),
                left + 300,
                top + 220,
                0xFFFFFFFF
        );

        super.render(graphics, mouseX, mouseY, delta);
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
