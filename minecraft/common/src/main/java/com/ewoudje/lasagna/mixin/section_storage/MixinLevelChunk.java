package com.ewoudje.lasagna.mixin.section_storage;

import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk extends ChunkAccess {


    public MixinLevelChunk(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Override
    public boolean isUnsaved() {
        return super.isUnsaved() || isStorageUnsaved();
    }

    private boolean isStorageUnsaved() {
        for (LevelChunkSection section : getSections()) {
            if (section instanceof ExtraStorageSectionContainer) {
                ExtraStorageSectionContainer container = (ExtraStorageSectionContainer) section;
                if (container.isDirty()) return true;
            }
        }

        return false;
    }
}
