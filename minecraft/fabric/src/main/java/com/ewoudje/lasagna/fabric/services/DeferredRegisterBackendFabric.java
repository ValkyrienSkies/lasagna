package com.ewoudje.lasagna.fabric.services;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import org.jetbrains.annotations.Nullable;
import com.ewoudje.lasagna.api.registry.AnonymousDeferredRegister;
import com.ewoudje.lasagna.fabric.DeferredRegisterImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import com.ewoudje.lasagna.api.registry.DeferredRegister;
import com.ewoudje.lasagna.fabric.LasagnaModFabric;
import com.ewoudje.lasagna.services.DeferredRegisterBackend;

public class DeferredRegisterBackendFabric implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl<>(id, registry);
    }

    @Override
    public <T> void makeUserRegistry(@NotNull Class<T> clazz, @NotNull ResourceKey<Registry<T>> registry) {
        LasagnaModFabric.track(FabricRegistryBuilder.createSimple(clazz, registry.location()).buildAndRegister());
    }

    @NotNull
    @Override
    public <T> AnonymousDeferredRegister<T> makeAnonymousDeferredRegister(@NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl<>(null, registry);
    }
}
