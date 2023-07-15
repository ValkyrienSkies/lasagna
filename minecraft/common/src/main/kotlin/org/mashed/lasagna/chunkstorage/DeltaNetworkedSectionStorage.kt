package org.mashed.lasagna.chunkstorage

import it.unimi.dsi.fastutil.ints.IntSets
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.chunk.LevelChunk
import org.mashed.lasagna.networking.LasagnaNetworking

// Be sure that the hash function is consistent
interface DeltaNetworkedSectionStorage: OwnedExtraSectionStorage {

    fun markDirty() {
        if (!dirtySections.contains(owner.pos.hashCode() * owner.sectionsCount + section)) {
            dirtyEntries.add(this)
        }
    }

    fun writeDeltaData(buf: FriendlyByteBuf)
    fun readDeltaData(buf: FriendlyByteBuf)

    companion object {
        private val dirtySections = IntSets.emptySet()
        private val dirtyEntries = mutableSetOf<DeltaNetworkedSectionStorage>()

        fun syncSections() {
            for (entry in dirtyEntries) {
                //LasagnaNetworking.send() TODO
            }

            dirtyEntries.clear()
            dirtySections.clear()
        }
    }
}