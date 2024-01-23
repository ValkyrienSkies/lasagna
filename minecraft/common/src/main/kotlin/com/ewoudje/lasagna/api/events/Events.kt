package com.ewoudje.lasagna.api.events

fun <T> makeEvent(): Event<T> = Event()

class Event<T> {
    private val listeners: MutableList<(T) -> Unit> = ArrayList()

    fun register(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    fun unregister(listener: (T) -> Unit) {
        listeners.remove(listener)
    }

    fun invoke(event: T) {
        listeners.forEach { it(event) }
    }
}