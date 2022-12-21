package me.ewoudje.lasagna.api.registry

interface RegistrySupplier<T> {

    val name: String

    fun get(): T
}