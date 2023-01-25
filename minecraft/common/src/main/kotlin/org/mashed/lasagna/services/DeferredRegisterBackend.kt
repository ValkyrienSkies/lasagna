package org.mashed.lasagna.services

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.mashed.lasagna.api.registry.DeferredRegister
import java.util.*

interface DeferredRegisterBackend {
    fun <T> makeDeferredRegister(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T>


    companion object : DeferredRegisterBackend by (
            ServiceLoader.load(DeferredRegisterBackend::class.java)
                .findFirst()
                .get())
}