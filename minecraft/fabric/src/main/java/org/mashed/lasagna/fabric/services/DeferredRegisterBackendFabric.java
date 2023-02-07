package org.mashed.lasagna.fabric.services;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import org.mashed.lasagna.fabric.DeferredRegisterImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.api.registry.DeferredRegister;
import org.mashed.lasagna.fabric.LasagnaModFabric;
import org.mashed.lasagna.services.DeferredRegisterBackend;

public class DeferredRegisterBackendFabric implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl<>(id, registry);
    }

    @NotNull
    @Override
    public <T> void makeUserRegistry(@NotNull Class<T> clazz, @NotNull ResourceKey<Registry<T>> registry) {
        LasagnaModFabric.track(FabricRegistryBuilder.createSimple(clazz, registry.location()).buildAndRegister());
    }
}
