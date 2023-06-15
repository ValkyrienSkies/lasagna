package org.mashed.lasagna.chunkstorage

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.mashed.lasagna.LasagnaMod.resource
import org.mashed.lasagna.api.registry.RegistryItem
import org.mashed.lasagna.api.registry.createUserRegistry
import org.mashed.lasagna.api.registry.getValue
import org.mashed.lasagna.api.registry.register

interface ExtraSectionStorage {
    fun writeNBT(nbt: CompoundTag): CompoundTag

    interface ExtraSectionStorageReader: RegistryItem<ExtraSectionStorageReader> {
        fun readNBT(nbt: CompoundTag): ExtraSectionStorage
    }

    companion object {
        val REGISTRY_KEY = ResourceKey.createRegistryKey<ExtraSectionStorageReader>("section_storage".resource)
        private val registry by (Registry.REGISTRY as Registry<Registry<ExtraSectionStorageReader>>).getOrCreateHolder(REGISTRY_KEY)
        private val deferred = createUserRegistry(REGISTRY_KEY)


        fun <T: ExtraSectionStorage> register(id: ResourceLocation, reader: (CompoundTag) -> T) {
            deferred.register(id) {
                object : ExtraSectionStorageReader {
                    override fun readNBT(nbt: CompoundTag): ExtraSectionStorage = reader(nbt)
                    override var id: ResourceLocation? = id
                }
            }
        }

        @JvmStatic
        fun readNbt(id: ResourceLocation, nbt: CompoundTag): ExtraSectionStorage {
            val reader = registry[id] ?: throw IllegalArgumentException("Unknown section storage type: $id")
            return reader.readNBT(nbt)
        }
    }
}