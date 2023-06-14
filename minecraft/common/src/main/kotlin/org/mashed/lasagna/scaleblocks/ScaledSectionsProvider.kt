package org.mashed.lasagna.scaleblocks

import net.minecraft.core.BlockPos

interface ScaledSectionsProvider {
    fun getScaledSectionsAt(pos: BlockPos): Set<ScaledSectionStorage>
}