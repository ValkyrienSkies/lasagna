package org.mashed.lasagna.mixin.scaleblocks;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.chunkstorage.ExtraSectionStorage;
import org.mashed.lasagna.scaleblocks.ScaledSection;
import org.mashed.lasagna.chunkstorage.ExtraStorageSectionContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(LevelChunkSection.class)
public class MixinLevelChunkSection implements ExtraStorageSectionContainer {
    @Unique
    private ScaledSection scaledSection = null;

    @Unique
    private List<ExtraSectionStorage> storage = new ArrayList<>();

    @NotNull
    @Override
    public ScaledSection getScaledSection() {
        return scaledSection;
    }

    @Override
    public void setScaledSection(ScaledSection scaledSection) {
        storage.add(scaledSection);
        this.scaledSection = scaledSection;
    }

    @NotNull
    @Override
    public List<ExtraSectionStorage> getStorage() {
        return storage;
    }
}
