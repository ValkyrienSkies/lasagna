package com.ewoudje.lasagna.api.registry

import com.ewoudje.lasagna.services.LasagnaPlatformHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.ServiceLoader

object CreativeTabs {
    fun create(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab {
        return LasagnaPlatformHelper.createCreativeTab(id, stack)
    }
}