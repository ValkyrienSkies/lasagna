package com.ewoudje.lasagna.api.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import com.ewoudje.lasagna.services.DeferredRegisterBackend

interface AnonymousDeferredRegister<T> : Iterable<RegistrySupplier<T>> {

    fun <I : T> register(name: ResourceLocation, builder: () -> I): RegistrySupplier<I>
    fun applyAll()

    companion object {
        operator fun <T> invoke(registry: ResourceKey<Registry<T>>): AnonymousDeferredRegister<T> =
            DeferredRegisterBackend.makeAnonymousDeferredRegister(registry)
    }
}