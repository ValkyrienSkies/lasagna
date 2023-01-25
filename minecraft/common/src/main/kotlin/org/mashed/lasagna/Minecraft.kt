package org.mashed.lasagna

import net.minecraft.client.gui.screens.worldselection.WorldPreset
import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.chunk.ChunkGenerator

val Minecraft = net.minecraft.client.Minecraft.getInstance()

val ClientLevel = Minecraft.level
val ClientPlayer = Minecraft.player

fun createWorldPreset(name: String, generate: (RegistryAccess, Long) -> ChunkGenerator) = object : WorldPreset(name) {
        override fun generator(registry: RegistryAccess, seed: Long): ChunkGenerator = generate(registry, seed)
    }