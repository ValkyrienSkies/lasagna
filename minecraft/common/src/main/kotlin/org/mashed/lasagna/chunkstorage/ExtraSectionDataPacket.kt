package org.mashed.lasagna.chunkstorage

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.chunk.LevelChunk
import org.mashed.lasagna.LasagnaMod.resource
import org.mashed.lasagna.networking.LasagnaNetworking
import org.mashed.lasagna.networking.defineSerialization

// Its actually for the whole chunk
class ExtraSectionDataPacket private constructor(
    private val chunk: LevelChunk?,
    private val data: ((ClientLevel) -> Array<Set<Map.Entry<ResourceLocation, ExtraSectionStorage>>?>)?
) {

    constructor(chunk: LevelChunk): this(chunk, null)

    fun write(buf: FriendlyByteBuf) {
        if (chunk == null) throw IllegalStateException("Chunk is null and were trying to send it to the client")
        val data = fromChunk(chunk)

        buf.writeChunkPos(chunk.pos)

        for (i in data.indices) {
            val storage = data[i]

            if (storage == null) {
                buf.writeVarInt(0)
                continue
            }

            buf.writeVarInt(storage.size)
            for ((id, sectionStorage) in storage) {
                buf.writeResourceLocation(id)
                sectionStorage.writePacket(buf, chunk.getSection(i))
            }
        }
    }

    companion object {
        private fun fromChunk(chunk: LevelChunk): Array<Set<Map.Entry<ResourceLocation, ExtraSectionStorage>>?> {
            val result = arrayOfNulls<Set<Map.Entry<ResourceLocation, ExtraSectionStorage>>?>(chunk.sectionsCount)
            val sections = chunk.sections

            for (i in sections.indices) {
                val storage = (sections[i] as ExtraStorageSectionContainer).getStorage()

                if (storage.isNotEmpty()) {
                    result[i] = storage
                }
            }

            return result
        }

        private fun parseBuffer(buf: FriendlyByteBuf): ExtraSectionDataPacket = ExtraSectionDataPacket(null) { level ->
            val pos = buf.readChunkPos()

            val chunk = level.getChunk(pos.x, pos.z)
            val result = arrayOfNulls<Set<Map.Entry<ResourceLocation, ExtraSectionStorage>>?>(level.sectionsCount)

            for (i in result.indices) {
                val count = buf.readVarInt()
                if (count == 0) continue

                val storage = mutableMapOf<ResourceLocation, ExtraSectionStorage>()

                for (j in 0 until count) {
                    val id = buf.readResourceLocation()
                    val sectionStorage = ExtraSectionStorage.readPacket(id, buf, chunk.sections[i])
                    (chunk.sections[i] as ExtraStorageSectionContainer).setSectionStorage(id, sectionStorage)
                    storage[id] = sectionStorage
                }

                result[i] = storage.entries
            }

            buf.release()
            return@ExtraSectionDataPacket result
        }

        fun register() {
            defineSerialization<ExtraSectionDataPacket>("extra_section_data".resource)
                { t, buf -> t.write(buf) } decode { buf -> buf.retain(); parseBuffer(buf) }

            LasagnaNetworking.packetClient { packet: ExtraSectionDataPacket, minecraft: Minecraft ->
                (packet.data ?: throw IllegalStateException("Packet data is null on client receiving"))
                    .invoke(minecraft.level!!)
            }
        }
    }
}