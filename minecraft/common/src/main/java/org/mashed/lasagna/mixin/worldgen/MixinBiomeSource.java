package org.mashed.lasagna.mixin.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import org.mashed.lasagna.worldgen.biome_source.TexturedBiomeSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
public class MixinBiomeSource {

    static {
        Registry.register(Registry.BIOME_SOURCE, "textured", TexturedBiomeSource.Companion.getCODEC());
    }
}
