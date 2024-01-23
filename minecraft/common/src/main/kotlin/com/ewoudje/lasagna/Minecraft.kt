package com.ewoudje.lasagna

import net.minecraft.client.gui.screens.worldselection.WorldPreset
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.levelgen.WorldGenSettings

val Minecraft = net.minecraft.client.Minecraft.getInstance()

val ClientLevel = Minecraft.level
val ClientPlayer = Minecraft.player

fun createWorldPreset(name: ResourceLocation,
                      generate: (RegistryAccess, Long) -> ChunkGenerator,
                      create: (RegistryAccess, Long, Boolean, Boolean, ChunkGenerator) -> WorldGenSettings = { registries, seed, generateFeatures, generateBonusChest, generator ->
                          WorldGenSettings(
                              seed, generateFeatures, generateBonusChest, WorldGenSettings.withOverworld(
                                  registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY),
                                  DimensionType.defaultDimensions(registries, seed),
                                  generator
                              )
                          )
                      }
) = object : WorldPreset(name.path) {
    val actualDescription = TranslatableComponent("world_preset.${name.namespace}.${name.path}")

    override fun description(): Component = actualDescription
    override fun generator(registry: RegistryAccess, seed: Long): ChunkGenerator = generate(registry, seed)
    override fun create(
        registryAccess: RegistryAccess,
        seed: Long,
        generateFeatures: Boolean,
        generateBonusChest: Boolean
    ): WorldGenSettings {
        val generator = generator(registryAccess, seed)
        return create(registryAccess, seed, generateFeatures, generateBonusChest, generator)
    }
}

operator inline fun <F, S> com.mojang.datafixers.util.Pair<F, S>.component1(): F = this.first
operator inline fun <F, S> com.mojang.datafixers.util.Pair<F, S>.component2(): S = this.second

inline fun whenClient(block: () -> Unit) {
    if (LasagnaMod.clientInitialized) {
        block()
    }
}