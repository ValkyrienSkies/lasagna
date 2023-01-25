package org.mashed.lasagna.api.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.mashed.lasagna.services.DeferredRegisterBackend
import java.util.ServiceLoader

interface DeferredRegister<T> : Iterable<RegistrySupplier<T>> {

    fun <I : T> register(name: String, builder: () -> I): RegistrySupplier<I>
    fun applyAll()

    companion object {

        fun <T> create(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T> =
            DeferredRegisterBackend.makeDeferredRegister(id, registry)
    }
}