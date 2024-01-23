package com.ewoudje.lasagna.api.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import com.ewoudje.lasagna.services.DeferredRegisterBackend
import java.util.ServiceLoader

interface DeferredRegister<T> : Iterable<RegistrySupplier<T>> {

    fun <I : T> register(name: String, builder: () -> I): RegistrySupplier<I>
    fun applyAll()

    companion object {

        operator fun <T> invoke(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T> =
            DeferredRegisterBackend.makeDeferredRegister(id, registry)
    }
}