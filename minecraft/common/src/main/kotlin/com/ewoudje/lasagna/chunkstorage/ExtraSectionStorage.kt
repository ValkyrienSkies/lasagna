package com.ewoudje.lasagna.chunkstorage

import com.ewoudje.lasagna.LasagnaMod.resource
import com.ewoudje.lasagna.api.registry.RegistryItem
import com.ewoudje.lasagna.api.registry.createUserRegistry
import com.ewoudje.lasagna.api.registry.getValue
import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.chunk.LevelChunk

/**
 * Extra section storage is a way to store additional data in a chunk section.
 * writeNBT is used for storing the data
 * writePacket is used for sending the data to the client if enabled
 *
 * If sync is enabled we will only send the initial data to the client and then
 * Its up to the end user to send delta updates.
 */
interface ExtraSectionStorage {
    fun isDirty(): Boolean
    fun saved()

    fun writeNBT(nbt: CompoundTag, chunk: LevelChunk, sectionIndex: Int): CompoundTag
    fun writePacket(buf: FriendlyByteBuf, chunk: LevelChunk, sectionIndex: Int) =
        buf.writeNbt(writeNBT(CompoundTag(), chunk, sectionIndex))

    interface ExtraSectionStorageEntry: RegistryItem<ExtraSectionStorageEntry> {
        fun readNBT(nbt: CompoundTag, chunk: LevelChunk, sectionIndex: Int): ExtraSectionStorage
        fun readPacket(buf: FriendlyByteBuf, chunk: LevelChunk, sectionIndex: Int): ExtraSectionStorage

        val sync: Boolean
    }

    companion object {
        val REGISTRY_KEY = ResourceKey.createRegistryKey<ExtraSectionStorageEntry>("section_storage".resource)
        private val registry by (Registry.REGISTRY as Registry<Registry<ExtraSectionStorageEntry>>).getOrCreateHolder(REGISTRY_KEY)
        private val deferred = createUserRegistry(REGISTRY_KEY)

        inline fun <reified T: ExtraSectionStorage> register(
            id: ResourceLocation,
            noinline reader: (CompoundTag, chunk: LevelChunk, sectionIndex: Int) -> T,
            sync: Boolean = false,
            noinline packetReader: (FriendlyByteBuf, LevelChunk, Int) -> T = { buf, chunk, index -> reader(buf.readNbt()!!, chunk, index) }
        ) = register(id, reader, sync, T::class.java, packetReader)

        fun <T: ExtraSectionStorage> register(
                id: ResourceLocation,
                reader: (CompoundTag, chunk: LevelChunk, sectionIndex: Int) -> T,
                sync: Boolean = false,
                clazz: Class<T>,
                packetReader: (FriendlyByteBuf, LevelChunk, Int) -> T = { buf, chunk, index -> reader(buf.readNbt()!!, chunk, index) }
        ) {
            deferred.register(id) {
                object : ExtraSectionStorageEntry {
                    override fun readNBT(nbt: CompoundTag, chunk: LevelChunk, sectionIndex: Int): ExtraSectionStorage =
                        reader(nbt, chunk, sectionIndex)
                    override fun readPacket(buf: FriendlyByteBuf, chunk: LevelChunk, sectionIndex: Int): ExtraSectionStorage =
                        packetReader(buf, chunk, sectionIndex)
                    override val sync: Boolean = sync
                    override var id: ResourceLocation? = id
                }
            }
        }

        @JvmStatic
        fun readNbt(id: ResourceLocation, nbt: CompoundTag, chunk: LevelChunk, sectionIndex: Int): ExtraSectionStorage {
            val reader = registry[id] ?: throw IllegalArgumentException("Unknown section storage type: $id")
            return reader.readNBT(nbt, chunk, sectionIndex)
        }

        @JvmStatic
        fun readPacket(id: ResourceLocation, buf: FriendlyByteBuf, chunk: LevelChunk, sectionIndex: Int): ExtraSectionStorage {
            val reader = registry[id] ?: throw IllegalArgumentException("Unknown section storage type: $id")
            return reader.readPacket(buf, chunk, sectionIndex)
        }

        init {
            ExtraSectionDataPacket.register()
        }
    }
}

interface OwnedExtraSectionStorage: ExtraSectionStorage {
    val owner: LevelChunk
    val section: Int
}