package com.ewoudje.lasagna.scaleblocks

import com.ewoudje.lasagna.api.Identifiable
import com.ewoudje.lasagna.chunkstorage.ExtraSectionStorage
import com.ewoudje.lasagna.kodec.codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.chunk.PalettedContainer

class ScaledSectionStorage(val view: ScaleBlocksView, override val id: ResourceLocation) : ExtraSectionStorage, Identifiable {
    val states: Array<PalettedContainer<BlockState>> = Array(view.resolution * view.resolution * view.resolution) {
        PalettedContainer(
            Block.BLOCK_STATE_REGISTRY,
            Blocks.AIR.defaultBlockState(),
            PalettedContainer.Strategy.SECTION_STATES
        )
    }
    var dirty = true

    fun getBlockState(x: Int, y: Int, z: Int): BlockState =
        states[getContainerIndex(x, y, z)].get(x % 16, y % 16, z % 16)


    fun setBlockState(x: Int, y: Int, z: Int, state: BlockState) {
        // TODO somewhere higher in the hierarchy, make LevelChunk enable the unsaved flag
        //assert(state.block is ScaledBlock && (state.block as ScaledBlock).supportsResolution(view.resolution))
        states[getContainerIndex(x, y, z)].set(x % 16, y % 16, z % 16, state)
    }

    private fun getContainerIndex(x: Int, y: Int, z: Int): Int {
        val xIndex = (x / 16) * view.resolution * view.resolution
        val yIndex = (y / 16) * view.resolution
        val zIndex = z / 16
        return xIndex + yIndex + zIndex
    }

    override fun isDirty(): Boolean = dirty
    override fun saved() { dirty = false }

    override fun writeNBT(storage: CompoundTag, chunk: LevelChunk, sectionIndex: Int): CompoundTag {
        storage.putString("id", id.toString())

        storage.put("view",
            ScaleBlocksView::class.codec.encode(view, NbtOps.INSTANCE, CompoundTag())
                .getOrThrow(false, ::RuntimeException)
        )

        // TODO write states

        return storage
    }

    companion object {
        @JvmStatic
        fun readNbt(storage: CompoundTag, chunk: LevelChunk, sectionIndex: Int): ScaledSectionStorage =
            ScaledSectionStorage(
                ScaleBlocksView::class.codec.decode(NbtOps.INSTANCE, storage.getCompound("view"))
                    .getOrThrow(false, ::RuntimeException).first,
                ResourceLocation(storage.getString("id"))
            ).apply {
                // TODO read states
            }

    }
}