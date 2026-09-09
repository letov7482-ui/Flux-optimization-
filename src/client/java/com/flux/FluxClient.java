package com.flux;

import com.flux.gui.FluxScreen;
import com.flux.performance.FramePacingEngine;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class FluxClient implements ClientModInitializer {

    public static final String MOD_ID = "flux";

    private static KeyMapping openGuiKey;

    private static final FramePacingEngine FRAME_PACING =
            new FramePacingEngine();

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

            if (openGuiKey.consumeClick()) {
                client.setScreen(new FluxScreen(client.screen));
            }
        });

        System.out.println("Flux Performance Engine initialized.");
    }

    public static FramePacingEngine getFramePacing() {
        return FRAME_PACING;
    }
}
