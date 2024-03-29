package com.ewoudje.lasagna.mixin.scaleblocks.client.accessor;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderChunk.class)
public interface AccessorRenderChunk {

    @Accessor("wrapped")
    LevelChunk getWrapped();

}
