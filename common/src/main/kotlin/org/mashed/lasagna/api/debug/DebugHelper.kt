package org.mashed.lasagna.api.debug

import net.minecraft.client.renderer.debug.DebugRenderer

object DebugHelper {
    internal val debugRenderers = mutableListOf<DebugRenderer.SimpleDebugRenderer>()
    fun useSwing() {
        System.setProperty("java.awt.headless", "false")
    }

    fun addDebugRenderer(debugRenderer: DebugRenderer.SimpleDebugRenderer) {
        debugRenderers.add(debugRenderer)
    }
}