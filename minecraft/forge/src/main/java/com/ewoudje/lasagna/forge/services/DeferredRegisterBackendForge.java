package com.ewoudje.lasagna.forge.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ewoudje.lasagna.api.registry.AnonymousDeferredRegister;
import com.ewoudje.lasagna.forge.AnonymousDeferredRegisterImpl;
import com.ewoudje.lasagna.forge.DeferredRegisterImpl;
import com.ewoudje.lasagna.api.registry.DeferredRegister;
import com.ewoudje.lasagna.forge.LasagnaModForge;
import com.ewoudje.lasagna.services.DeferredRegisterBackend;

public class DeferredRegisterBackendForge implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@Nullable String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl(id, registry);
    }

    @Override
    public <T> void makeUserRegistry(@NotNull Class<T> clazz, @NotNull ResourceKey<Registry<T>> registry) {
        LasagnaModForge.makeRegistry(clazz, registry);
    }

    @NotNull
    @Override
    public <T> AnonymousDeferredRegister<T> makeAnonymousDeferredRegister(@NotNull ResourceKey<Registry<T>> registry) {
        return new AnonymousDeferredRegisterImpl(registry);
    }
}
