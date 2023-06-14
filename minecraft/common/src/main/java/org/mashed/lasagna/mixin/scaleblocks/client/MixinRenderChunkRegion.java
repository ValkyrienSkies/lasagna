package org.mashed.lasagna.mixin.scaleblocks.client;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.mixin.scaleblocks.client.accessor.AccessorRenderChunk;
import org.mashed.lasagna.scaleblocks.ScaledSectionStorage;
import org.mashed.lasagna.chunkstorage.ExtraStorageSectionContainer;
import org.mashed.lasagna.scaleblocks.ScaledSectionsProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;

@Mixin(RenderChunkRegion.class)
public class MixinRenderChunkRegion implements ScaledSectionsProvider {

    @Shadow @Final private int centerX;

    @Shadow @Final private int centerZ;

    @Shadow @Final protected net.minecraft.client.renderer.chunk.RenderChunk[][] chunks;

    @Shadow @Final protected Level level;

    @Override
    public Set<ScaledSectionStorage> getScaledSectionsAt(@NotNull BlockPos pos) {
        int i = SectionPos.blockToSectionCoord(pos.getX()) - this.centerX;
        int j = SectionPos.blockToSectionCoord(pos.getZ()) - this.centerZ;
        LevelChunk chunk = ((AccessorRenderChunk) this.chunks[i][j]).getWrapped();
        int index = level.getSectionIndex(pos.getY());
        return ((ExtraStorageSectionContainer) chunk.getSection(index)).getSectionsOfType(ScaledSectionStorage.class);
    }
}