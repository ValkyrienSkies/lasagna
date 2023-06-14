package org.mashed.lasagna.mixin.scaleblocks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.mashed.lasagna.scaleblocks.ScaledSectionsProvider;
import org.mashed.lasagna.scaleblocks.render.ScaledSectionRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;
import java.util.Set;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class MixinRenderChunk {

    // The 'this' of the outer class
    @Shadow @Final ChunkRenderDispatcher.RenderChunk field_20839;

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getBlockRenderer()Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void compile(float x, float y, float z, ChunkRenderDispatcher.CompiledChunk compiledChunk, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph visGraph, Set set, RenderChunkRegion region, PoseStack poseStack, Random random) {
        var sections = ((ScaledSectionsProvider) region).getScaledSectionsAt(this.field_20839.getOrigin());

        for (var section : sections) {
            ScaledSectionRenderer.compile(section, x, y, z, compiledChunk, buffers);
        }
    }

}
