package org.mashed.lasagna.api.events

object RegistryEvents {
    // TODO do not get executed
    val onTagsLoaded = makeEvent<Unit>()
    val onRegistriesComplete = makeEvent<Unit>()
}