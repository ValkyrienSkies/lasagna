package org.mashed.lasagna.chunkstorage

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation

interface ExtraSectionStorage {
    val id: ResourceLocation

    fun writeNBT(nbt: CompoundTag): CompoundTag

    companion object {

        @JvmStatic
        fun readNbt(nbt: CompoundTag): ExtraSectionStorage {
            TODO("Not yet implemented")
        }
    }
}