package org.mashed.lasagna.services

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.mashed.lasagna.api.registry.DeferredRegister

interface DeferredRegisterBackend {
    fun <T> makeDeferredRegister(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T>
}