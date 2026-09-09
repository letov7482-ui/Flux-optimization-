package com.flux;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class FluxInput {

    private FluxInput() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FluxClient.handleKeyInput();
        });
    }
}
