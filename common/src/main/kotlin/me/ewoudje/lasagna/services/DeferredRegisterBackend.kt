package me.ewoudje.lasagna.services

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import me.ewoudje.lasagna.api.registry.DeferredRegister

interface DeferredRegisterBackend {
    fun <T> makeDeferredRegister(id: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T>
}