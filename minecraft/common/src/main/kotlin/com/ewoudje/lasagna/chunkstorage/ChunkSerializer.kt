package com.ewoudje.lasagna.chunkstorage

import com.ewoudje.lasagna.chunkstorage.ExtraSectionStorage.Companion.readNbt
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.chunk.LevelChunk
import java.util.function.Consumer

object ChunkSerializerHelper {
    fun read(
        chunk: LevelChunk,
        sectionNbt: CompoundTag,
        index: Int,
    ) {
        if (sectionNbt.contains("lasagna:ExtraStorage")) {
            val section = chunk.sections[index] as ExtraStorageSectionContainer
            val extraStorage = sectionNbt.getList("lasagna:ExtraStorage", Tag.TAG_COMPOUND.toInt())
            extraStorage.forEach(Consumer { xTag: Tag ->
                val id = ResourceLocation.tryParse((xTag as CompoundTag).getString("type"))!!
                section.setSectionStorage(id, readNbt(id, xTag, chunk, index))
            })
        }
    }

    fun write(
        chunk: LevelChunk,
        index: Int,
        nbt: CompoundTag,
    ) {
        val storage = (chunk.sections[index] as ExtraStorageSectionContainer).getStorage()
        if (storage.isNotEmpty()) {
            val extraStorage = ListTag()
            storage.forEach(Consumer { (key, value): Map.Entry<ResourceLocation, ExtraSectionStorage> ->
                var tag = CompoundTag()
                tag.putString("type", key.toString())
                tag = value.writeNBT(tag, chunk, index)
                value.saved()
                extraStorage.add(tag)
            })
            nbt.put("lasagna:ExtraStorage", extraStorage)
        }
    }
}