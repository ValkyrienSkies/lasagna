package org.mashed.lasagna.services

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.mashed.lasagna.api.registry.AnonymousDeferredRegister
import org.mashed.lasagna.api.registry.DeferredRegister
import java.util.*

interface DeferredRegisterBackend {
    fun <T> makeAnonymousDeferredRegister(registry: ResourceKey<Registry<T>>): AnonymousDeferredRegister<T>
    fun <T> makeDeferredRegister(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T>
    fun <T> makeUserRegistry(clazz: Class<T>, registry: ResourceKey<Registry<T>>): Unit

    companion object : DeferredRegisterBackend by (
            ServiceLoader.load(DeferredRegisterBackend::class.java)
                .findFirst()
                .get()) {
        inline fun <reified T> makeUserRegistry(registry: ResourceKey<Registry<T>>): Unit =
            makeUserRegistry(T::class.java, registry)
    }
}