package org.mashed.lasagna.forge.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.forge.DeferredRegisterImpl;
import org.mashed.lasagna.api.registry.DeferredRegister;
import org.mashed.lasagna.forge.LasagnaModForge;
import org.mashed.lasagna.services.DeferredRegisterBackend;

public class DeferredRegisterBackendForge implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl(id, registry);
    }

    @NotNull
    @Override
    public <T> void makeUserRegistry(@NotNull Class<T> clazz, @NotNull ResourceKey<Registry<T>> registry) {
        LasagnaModForge.makeRegistry(clazz, registry);
    }
}
