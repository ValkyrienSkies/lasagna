package org.mashed.lasagna.scaleblocks

import com.mojang.blaze3d.vertex.PoseStack
import me.crackhead.potato_battery.render.RenderTypes
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.ChunkBufferBuilderPack
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.PalettedContainer
import org.mashed.lasagna.Minecraft

class ScaledSection(val view: ScaleBlocksView) {
    private val states: Array<PalettedContainer<BlockState>> = Array(view.resolution * view.resolution * view.resolution) {
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

    @Environment(net.fabricmc.api.EnvType.CLIENT)
    fun compile(
        x: Float,
        y: Float,
        z: Float,
        compiledChunk: ChunkRenderDispatcher.CompiledChunk,
        buffers: ChunkBufferBuilderPack
    ) {
        val renderer = Minecraft.blockRenderer
        val posestack = PoseStack()
        val buffer = buffers.builder(RenderTypes.solid)
        val scale = 1.0f / view.resolution

        repeat(view.resolution) { xIndex ->
            repeat(view.resolution) { yIndex ->
                repeat(view.resolution) { zIndex ->
                    val cont = states[xIndex * view.resolution * view.resolution + yIndex * view.resolution + zIndex]

                    posestack.pushPose()
                    posestack.scale(scale, scale, scale)
                    posestack.translate(xIndex * 16.0, yIndex * 16.0, zIndex * 16.0)

                    repeat(16) { x ->
                        repeat(16) { y ->
                            repeat(16) { z ->
                                val state = cont.get(x, y, z)
                                if (state != Blocks.AIR.defaultBlockState()) {
                                    posestack.pushPose()
                                    posestack.translate(x.toDouble(), y.toDouble(), z.toDouble())
                                    val model = renderer.getBlockModel(state)
                                    // renderer.renderBatched(state, pos, ) TODO optimize to use batched rendering
                                    renderer.modelRenderer.renderModel(
                                        posestack.last(),
                                        buffer,
                                        state,
                                        model,
                                        1f,
                                        1f,
                                        1f,
                                        15,
                                        OverlayTexture.NO_OVERLAY
                                    )
                                    posestack.popPose()
                                }
                            }
                        }
                    }

                    posestack.popPose()
                }
            }
        }
    }
}