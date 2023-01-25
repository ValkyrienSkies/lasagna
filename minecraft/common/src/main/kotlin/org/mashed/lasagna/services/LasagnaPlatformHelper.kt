package org.mashed.lasagna.services

import net.minecraft.client.gui.screens.worldselection.WorldPreset
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.*

interface LasagnaPlatformHelper {

    fun createCreativeTab(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab

    fun worldPresetsRegistry(): ResourceKey<Registry<WorldPreset>>


    companion object : LasagnaPlatformHelper by (
            ServiceLoader.load(LasagnaPlatformHelper::class.java)
                .findFirst()
                .get())
}