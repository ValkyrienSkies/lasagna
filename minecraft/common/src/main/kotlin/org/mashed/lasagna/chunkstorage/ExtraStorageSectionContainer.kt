package org.mashed.lasagna.chunkstorage

import org.mashed.lasagna.scaleblocks.ScaledSection

interface ExtraStorageSectionContainer {
    val storage: List<ExtraSectionStorage>
    @Deprecated("Use storage instead")
    var scaledSection: ScaledSection?

}