package com.ewoudje.lasagna.chunkstorage

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.LevelChunkSection


interface PerBlockSectionStorage<T>: ExtraSectionStorage {
    operator fun get(x: Int, y: Int, z: Int): T
    operator fun set(x: Int, y: Int, z: Int, value: T)
}

abstract class PerBlockNetworkedSectionStorage<T> : PerBlockSectionStorage<T>, DeltaNetworkedSectionStorage {

    override operator fun set(x: Int, y: Int, z: Int, value: T) {
        markDirty(x, y, z)
        storageSet(x, y, z, value)
    }

    abstract fun storageSet(x: Int, y: Int, z: Int, value: T)
    abstract fun markDirty(x: Int, y: Int, z: Int)
}

abstract class FullSyncPerBlockNetworkedSectionStorage<T> : PerBlockNetworkedSectionStorage<T>() {
    override fun markDirty(x: Int, y: Int, z: Int) { markDirty() }

    override fun writePacket(buf: FriendlyByteBuf, chunk: LevelChunk, index: Int): FriendlyByteBuf {
        storagePacketWrite(buf, chunk, index)
        return buf
    }

    abstract fun storagePacketWrite(buf: FriendlyByteBuf, chunk: LevelChunk, index: Int)
}