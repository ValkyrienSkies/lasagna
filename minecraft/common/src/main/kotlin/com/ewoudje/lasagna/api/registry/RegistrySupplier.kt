package com.ewoudje.lasagna.api.registry

import net.minecraft.core.Holder

interface RegistrySupplier<T> {

    val name: String

    fun get(): T

    fun holder(): Holder<T>
}