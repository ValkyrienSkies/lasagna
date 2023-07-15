package org.mashed.lasagna.chunkstorage

import net.minecraft.core.Registry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.LevelChunkSection
import org.mashed.lasagna.LasagnaMod.resource
import org.mashed.lasagna.api.registry.RegistryItem
import org.mashed.lasagna.api.registry.createUserRegistry
import org.mashed.lasagna.api.registry.getValue
import kotlin.text.Typography.section

/**
 * Extra section storage is a way to store additional data in a chunk section.
 * writeNBT is used for storing the data
 * writePacket is used for sending the data to the client if enabled
 *
 * If sync is enabled we will only send the initial data to the client and then
 * Its up to the end user to send delta updates.
 */
interface ExtraSectionStorage {
    fun writeNBT(nbt: CompoundTag, section: LevelChunkSection): CompoundTag
    fun writePacket(buf: FriendlyByteBuf, section: LevelChunkSection) =
        buf.writeNbt(writeNBT(CompoundTag(), section))

    interface ExtraSectionStorageEntry: RegistryItem<ExtraSectionStorageEntry> {
        fun readNBT(nbt: CompoundTag, section: LevelChunkSection): ExtraSectionStorage
        fun readPacket(buf: FriendlyByteBuf, section: LevelChunkSection): ExtraSectionStorage

        val sync: Boolean
    }

    companion object {
        val REGISTRY_KEY = ResourceKey.createRegistryKey<ExtraSectionStorageEntry>("section_storage".resource)
        private val registry by (Registry.REGISTRY as Registry<Registry<ExtraSectionStorageEntry>>).getOrCreateHolder(REGISTRY_KEY)
        private val deferred = createUserRegistry(REGISTRY_KEY)

        inline fun <reified T: ExtraSectionStorage> register(
            id: ResourceLocation,
            noinline reader: (CompoundTag, section: LevelChunkSection) -> T,
            sync: Boolean = false,
            noinline packetReader: (FriendlyByteBuf, LevelChunkSection) -> T = { buf, section -> reader(buf.readNbt()!!, section) }
        ) = register(id, reader, sync, T::class.java, packetReader)

        fun <T: ExtraSectionStorage> register(
                id: ResourceLocation,
                reader: (CompoundTag, section: LevelChunkSection) -> T,
                sync: Boolean = false,
                clazz: Class<T>,
                packetReader: (FriendlyByteBuf, LevelChunkSection) -> T = { buf, section -> reader(buf.readNbt()!!, section) }
        ) {
            deferred.register(id) {
                object : ExtraSectionStorageEntry {
                    override fun readNBT(nbt: CompoundTag, section: LevelChunkSection): ExtraSectionStorage = reader(nbt, section)
                    override fun readPacket(buf: FriendlyByteBuf, section: LevelChunkSection): ExtraSectionStorage = packetReader(buf, section)
                    override val sync: Boolean = sync
                    override var id: ResourceLocation? = id
                }
            }
        }

        @JvmStatic
        fun readNbt(id: ResourceLocation, nbt: CompoundTag, section: LevelChunkSection): ExtraSectionStorage {
            val reader = registry[id] ?: throw IllegalArgumentException("Unknown section storage type: $id")
            return reader.readNBT(nbt, section)
        }

        @JvmStatic
        fun readPacket(id: ResourceLocation, buf: FriendlyByteBuf, section: LevelChunkSection): ExtraSectionStorage {
            val reader = registry[id] ?: throw IllegalArgumentException("Unknown section storage type: $id")
            return reader.readPacket(buf, section)
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