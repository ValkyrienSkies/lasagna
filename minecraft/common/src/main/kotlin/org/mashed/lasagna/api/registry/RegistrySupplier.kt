package org.mashed.lasagna.api.registry

interface RegistrySupplier<T> {

    val name: String

    fun get(): T
}