package com.ewoudje.lasagna.chunkstorage

import net.minecraft.resources.ResourceLocation
import com.ewoudje.lasagna.api.Identifiable

interface ExtraStorageSectionContainer {
    fun getSectionStorage(id: ResourceLocation): ExtraSectionStorage?
    fun setSectionStorage(id: ResourceLocation, storage: ExtraSectionStorage)
    fun removeSectionStorage(id: ResourceLocation): ExtraSectionStorage?

    fun <T> getSectionsOfType(type: Class<T>): Set<T> where T: ExtraSectionStorage
    fun getStorage(): Set<Map.Entry<ResourceLocation, ExtraSectionStorage>>
}

inline fun <reified T> ExtraStorageSectionContainer.setSectionStorage(storage: T) where T: ExtraSectionStorage, T: Identifiable {
    this.setSectionStorage(storage.id, storage)
}