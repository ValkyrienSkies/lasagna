package org.mashed.lasagna.scaleblocks

import net.minecraft.core.BlockPos

interface ScaledSectionsProvider {
    fun getScaledSectionAt(pos: BlockPos): ScaledSection?
}