package org.mashed.lasagna.mixin;

import kotlin.Unit;
import net.minecraft.core.Registry;
import org.mashed.lasagna.api.events.RegistryEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registry.class)
public class MixinRegistry {

    @Inject(at = @At("TAIL"), method = "freezeBuiltins")
    private static void afterRegistries(CallbackInfo ci) {
        RegistryEvents.INSTANCE.getOnRegistriesComplete().invoke(Unit.INSTANCE);
    }
}
