package org.mashed.lasagna.api.registry

import net.minecraft.resources.ResourceLocation

interface RegistryItem<I> {

    var id: ResourceLocation?
}