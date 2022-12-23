package org.mashed.lasagna.api.events

object RuntimeEvents {
    val onClientBoot = makeEvent<Unit>()
}