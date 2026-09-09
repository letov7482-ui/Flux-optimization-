package com.flux;

import com.flux.gui.FluxScreen;
import com.flux.performance.AdaptivePerformanceEngine;
import com.flux.performance.FramePacingEngine;
import com.flux.performance.OptimizationEngine;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class FluxClient implements ClientModInitializer {

    public static final String MOD_ID = "flux";

    private static KeyMapping openGuiKey;

    private static final FramePacingEngine FRAME_PACING =
            new FramePacingEngine();

    private static final AdaptivePerformanceEngine ADAPTIVE_PERFORMANCE =
            new AdaptivePerformanceEngine();

    private static final OptimizationEngine OPTIMIZATION =
            new OptimizationEngine();

    @Override
    public void onInitializeClient() {

        openGuiKey = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.flux.open_gui",
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        "category.flux"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            ADAPTIVE_PERFORMANCE.tick(FRAME_PACING);

            OPTIMIZATION.update(ADAPTIVE_PERFORMANCE);

            if (openGuiKey.consumeClick()) {
                client.setScreen(new FluxScreen(client.screen));
            }
        });

        System.out.println(
                "Flux Performance Engine initialized."
        );
    }

    public static FramePacingEngine getFramePacing() {
        return FRAME_PACING;
    }

    public static AdaptivePerformanceEngine getAdaptivePerformance() {
        return ADAPTIVE_PERFORMANCE;
    }

    public static OptimizationEngine getOptimization() {
        return OPTIMIZATION;
    }
}
