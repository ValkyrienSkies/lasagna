package org.mashed.lasagna.mixin.scaleblocks;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.mixin.scaleblocks.accessor.AccessorRenderChunk;
import org.mashed.lasagna.scaleblocks.ScaledSection;
import org.mashed.lasagna.scaleblocks.ScaledSectionContainer;
import org.mashed.lasagna.scaleblocks.ScaledSectionsProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderChunkRegion.class)
public class MixinRenderChunkRegion implements ScaledSectionsProvider {

    @Shadow @Final private int centerX;

    @Shadow @Final private int centerZ;

    @Shadow @Final protected net.minecraft.client.renderer.chunk.RenderChunk[][] chunks;

    @Shadow @Final protected Level level;

    @Override
    public ScaledSection getScaledSectionAt(@NotNull BlockPos pos) {
        int i = SectionPos.blockToSectionCoord(pos.getX()) - this.centerX;
        int j = SectionPos.blockToSectionCoord(pos.getZ()) - this.centerZ;
        return ((ScaledSectionContainer) ((AccessorRenderChunk) this.chunks[i][j]).getWrapped().getSection(level.getSectionIndex(pos.getY()))).getScaledSection();
    }
}