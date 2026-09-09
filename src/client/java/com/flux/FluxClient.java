package com.flux;

import com.flux.gui.FluxScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class FluxClient implements ClientModInitializer {

    public static final String MOD_ID = "flux";

    private static KeyMapping openGuiKey;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.flux.open_gui",
                        GLFW.GLFW_KEY_RIGHT_SHIFT,
                        "category.flux"
                )
        );

        System.out.println("Flux Performance Engine initialized.");
    }

    public static void handleKeyInput() {
        Minecraft minecraft = Minecraft.getInstance();

        if (openGuiKey.consumeClick()) {
            minecraft.setScreen(new FluxScreen(minecraft.screen));
        }
    }

    public static KeyMapping getOpenGuiKey() {
        return openGuiKey;
    }
}
