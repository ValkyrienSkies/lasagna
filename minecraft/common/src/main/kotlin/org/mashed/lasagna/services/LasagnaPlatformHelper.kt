package org.mashed.lasagna.services

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screens.worldselection.WorldPreset
import net.minecraft.client.renderer.DimensionSpecialEffects
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.mashed.lasagna.networking.Serialization
import org.mashed.lasagna.networking.ToClientPacketTarget
import java.util.*

interface LasagnaPlatformHelper {

    fun createCreativeTab(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab

    fun registerDataListener(id: ResourceLocation, listener: PreparableReloadListener)

    @Environment(EnvType.SERVER)
    fun <T> sendToClient(serialization: Serialization<T>, target: ToClientPacketTarget, data: T)

    @Environment(EnvType.CLIENT)
    fun worldPresetsRegistry(): ResourceKey<Registry<WorldPreset>>

    @Environment(EnvType.CLIENT)
    fun dimensionEffectsRegistry(): ResourceKey<Registry<DimensionSpecialEffects>>

    @Environment(EnvType.CLIENT)
    fun <T> sendToServer(serialization: Serialization<T>, data: T)

    companion object : LasagnaPlatformHelper by (
            ServiceLoader.load(LasagnaPlatformHelper::class.java)
                .findFirst()
                .get())
}