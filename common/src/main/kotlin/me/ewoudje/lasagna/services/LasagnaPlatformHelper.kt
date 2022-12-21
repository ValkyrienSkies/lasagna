package me.ewoudje.lasagna.services

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

interface LasagnaPlatformHelper {

    fun createCreativeTab(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab

}