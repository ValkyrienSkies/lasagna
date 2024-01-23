package com.ewoudje.lasagna.api.registry

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import com.ewoudje.lasagna.services.LasagnaPlatformHelper

@Environment(EnvType.CLIENT)
object SpecialClientRegistries {

    val WORLD_PRESETS = LasagnaPlatformHelper.worldPresetsRegistry()
    val DIMENSION_SPECIAL_EFFECTS =  LasagnaPlatformHelper.dimensionEffectsRegistry()

}