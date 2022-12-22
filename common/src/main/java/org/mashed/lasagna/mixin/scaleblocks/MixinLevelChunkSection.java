package org.mashed.lasagna.mixin.scaleblocks;

import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.scaleblocks.ScaledSection;
import org.mashed.lasagna.scaleblocks.ScaledSectionContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunkSection.class)
public class MixinLevelChunkSection implements ScaledSectionContainer {
    @Unique
    private ScaledSection scaledSection = null;

    @NotNull
    @Override
    public ScaledSection getScaledSection() {
        return scaledSection;
    }

    @Override
    public void setScaledSection(ScaledSection scaledSection) {
        this.scaledSection = scaledSection;
    }
}
