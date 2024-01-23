package com.ewoudje.lasagna.scaleblocks

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

// scaleDown = 2 means 0.5 scale thus 8 spots in a block
// scaleDown = 4 means 0.25 scale thus 16 spots in a block
data class ScaleBlocksView(val resolution: Int, val margin: Double)
