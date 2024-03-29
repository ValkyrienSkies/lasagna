package com.ewoudje.lasagna.mixin.scaleblocks.client;

import com.ewoudje.lasagna.scaleblocks.ScaledSectionStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.ewoudje.lasagna.scaleblocks.ScaledSectionsProvider;
import com.ewoudje.lasagna.scaleblocks.render.ScaledSectionRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique private Set<ScaledSectionStorage> lasagna_lib$storage = null;

    @Inject(method = "compile", at =
        @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getBlockRenderer()Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void getSections(float x, float y, float z, ChunkRenderDispatcher.CompiledChunk compiledChunk, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph visGraph, Set set, RenderChunkRegion region, PoseStack poseStack, Random random) {
        lasagna_lib$storage = ((ScaledSectionsProvider) region).getScaledSectionsAt(this.field_20839.getOrigin());
    }

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Ljava/util/Set;stream()Ljava/util/stream/Stream;"))
    private void renderSections(float x, float y, float z, ChunkRenderDispatcher.CompiledChunk compiledChunk, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<Set<BlockEntity>> cir) {
        if (compiledChunk.isEmpty(RenderType.solid())) return; // TODO make the layer if it not exists instead of just not rendering it

        for (var section : lasagna_lib$storage) {
            ScaledSectionRenderer.compile(section, x, y, z, compiledChunk, buffers);
        }

        lasagna_lib$storage = null;
    }

}
