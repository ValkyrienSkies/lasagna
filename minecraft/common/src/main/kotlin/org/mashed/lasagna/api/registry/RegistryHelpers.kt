package org.mashed.lasagna.api.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.mashed.lasagna.services.DeferredRegisterBackend

inline fun <reified T> createUserRegistry(registry: ResourceKey<Registry<T>>) =
    DeferredRegisterBackend.makeUserRegistry(registry)