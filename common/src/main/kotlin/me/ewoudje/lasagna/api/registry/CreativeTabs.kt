package me.ewoudje.lasagna.api.registry

import me.ewoudje.lasagna.services.LasagnaPlatformHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.ServiceLoader

object CreativeTabs {
    fun create(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab {
        return ServiceLoader.load(LasagnaPlatformHelper::class.java)
            .findFirst()
            .get()
            .createCreativeTab(id, stack)
    }
}