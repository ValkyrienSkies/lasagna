package me.ewoudje.lasagna.api.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import me.ewoudje.lasagna.services.DeferredRegisterBackend
import java.util.ServiceLoader

interface DeferredRegister<T> : Iterable<RegistrySupplier<T>> {

    fun <I : T> register(name: String, builder: () -> I): RegistrySupplier<I>
    fun applyAll()

    companion object {
        private val backend: DeferredRegisterBackend = load()

        fun <T> create(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T> =
            backend.makeDeferredRegister(id, registry)

        private fun load(): DeferredRegisterBackend =
            ServiceLoader.load(DeferredRegisterBackend::class.java)
                .findFirst()
                .orElseThrow { NullPointerException("Failed to load service for DeferredRegisterBackend") }
    }
}