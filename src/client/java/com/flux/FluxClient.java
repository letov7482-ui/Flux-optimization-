package com.flux;

import net.fabricmc.api.ClientModInitializer;

public class FluxClient implements ClientModInitializer {

    public static final String MOD_ID = "flux";

    @Override
    public void onInitializeClient() {
        System.out.println();
        System.out.println("================================");
        System.out.println("            F L U X");
        System.out.println("      PERFORMANCE ENGINE");
        System.out.println("================================");
        System.out.println("Flux initialized successfully.");
        System.out.println("================================");
        System.out.println();
    }
}
