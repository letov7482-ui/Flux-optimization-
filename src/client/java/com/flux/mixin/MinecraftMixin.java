package com.flux.mixin;

import com.flux.FluxClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
            method = "runTick",
            at = @At("HEAD")
    )
    private void flux$beginFrame(boolean tick, CallbackInfo ci) {
        FluxClient.getFramePacing().beginFrame();
    }
}
