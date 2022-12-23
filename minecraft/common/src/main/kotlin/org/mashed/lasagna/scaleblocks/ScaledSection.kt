package org.mashed.lasagna.scaleblocks

import com.mojang.blaze3d.vertex.PoseStack
import me.crackhead.potato_battery.render.RenderTypes
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.ChunkBufferBuilderPack
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.PalettedContainer
import org.mashed.lasagna.Minecraft
import org.mashed.lasagna.kodec.codec
import java.lang.RuntimeException

class ScaledSection(val view: ScaleBlocksView) {
    val states: Array<PalettedContainer<BlockState>> = Array(view.resolution * view.resolution * view.resolution) {
        PalettedContainer(
            Block.BLOCK_STATE_REGISTRY,
            Blocks.AIR.defaultBlockState(),
            PalettedContainer.Strategy.SECTION_STATES
        )
    }

    fun getBlockState(x: Int, y: Int, z: Int): BlockState =
        states[getContainerIndex(x, y, z)].get(x % 16, y % 16, z % 16)


    fun setBlockState(x: Int, y: Int, z: Int, state: BlockState) {
        assert(state.block is ScaledBlock && (state.block as ScaledBlock).supportsResolution(view.resolution))
        states[getContainerIndex(x, y, z)].set(x % 16, y % 16, z % 16, state)
    }

    private fun getContainerIndex(x: Int, y: Int, z: Int): Int {
        val xIndex = (x / 16) * view.resolution * view.resolution
        val yIndex = (y / 16) * view.resolution
        val zIndex = z / 16
        return xIndex + yIndex + zIndex
    }

    fun writeNbt(section: CompoundTag) {
        section.put("scaled_view",
            ScaleBlocksView::class.codec.encode(view, NbtOps.INSTANCE, CompoundTag())
                .getOrThrow(false, ::RuntimeException)
        )

        // TODO write states
    }

    companion object {
        @JvmStatic
        fun readNbt(section: CompoundTag): ScaledSection? =
            if (section.contains("scaled_view")) {
                ScaledSection(ScaleBlocksView::class.codec.decode(NbtOps.INSTANCE, section.getCompound("scaled_view"))
                    .getOrThrow(false, ::RuntimeException).first).apply {
                        // TODO read states
                }
            } else null

    }
}