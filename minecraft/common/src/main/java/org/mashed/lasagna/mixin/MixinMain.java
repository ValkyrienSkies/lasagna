package org.mashed.lasagna.mixin;

import org.mashed.lasagna.api.events.RuntimeEvents;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MixinMain {

    @Inject(at = @At("HEAD"), method = "main", remap = false)
    private static void main(String[] strings, CallbackInfo ci) {
        RuntimeEvents.INSTANCE.getOnClientBoot().invoke(null);
    }

}
