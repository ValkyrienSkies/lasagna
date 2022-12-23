package org.mashed.lasagna.scaleblocks.render

import com.mojang.blaze3d.vertex.PoseStack
import me.crackhead.potato_battery.render.RenderTypes
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.ChunkBufferBuilderPack
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.level.block.Blocks
import org.mashed.lasagna.Minecraft
import org.mashed.lasagna.scaleblocks.ScaledSection

@Environment(net.fabricmc.api.EnvType.CLIENT)
object ScaledSectionRenderer {

    @JvmStatic
    fun compile(
        section: ScaledSection,
        x: Float,
        y: Float,
        z: Float,
        compiledChunk: ChunkRenderDispatcher.CompiledChunk,
        buffers: ChunkBufferBuilderPack
    ) {
        val view = section.view
        val renderer = Minecraft.blockRenderer
        val posestack = PoseStack()
        val buffer = buffers.builder(RenderTypes.solid)
        val scale = (1.0f - (view.margin.toFloat() * 2)) / view.resolution
        posestack.translate(view.margin, view.margin, view.margin)
        posestack.scale(scale, scale, scale)

        repeat(view.resolution) { xIndex ->
            repeat(view.resolution) { yIndex ->
                repeat(view.resolution) { zIndex ->
                    val cont = section.states[xIndex * view.resolution * view.resolution + yIndex * view.resolution + zIndex]

                    posestack.pushPose()
                    posestack.translate(xIndex * 16.0, yIndex * 16.0, zIndex * 16.0)

                    repeat(16) { x ->
                        repeat(16) { y ->
                            repeat(16) { z ->
                                val state = cont.get(x, y, z)
                                if (state != Blocks.AIR.defaultBlockState()) {
                                    posestack.pushPose()

                                    val paddingMul = (view.margin / scale) * 2
                                    posestack.translate(
                                        x.toDouble() + (paddingMul * (x / view.resolution)),
                                        y.toDouble() + (paddingMul * (y / view.resolution)),
                                        z.toDouble() + (paddingMul * (z / view.resolution))
                                    )

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