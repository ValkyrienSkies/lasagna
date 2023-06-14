package org.mashed.lasagna.api.registry

import com.mojang.serialization.Codec
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.mashed.lasagna.services.DeferredRegisterBackend

inline fun <reified T: RegistryItem<T>> createUserRegistry(registry: ResourceKey<Registry<T>>): Holder<Registry<T>> {
    DeferredRegisterBackend.makeUserRegistry(registry)
    return (Registry.REGISTRY as Registry<Any>).getOrCreateHolder(registry as ResourceKey<Any>) as Holder<Registry<T>>
}

inline fun <reified T> Registry<T>.register(id: ResourceLocation, value: T): T = Registry.register(this, id, value)

operator fun <T> Holder<T>.getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): T = this.value()