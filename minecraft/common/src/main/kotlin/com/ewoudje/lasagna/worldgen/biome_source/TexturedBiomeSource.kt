package com.ewoudje.lasagna.worldgen.biome_source

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.ListCodec
import com.mojang.serialization.codecs.PairCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.Climate
import com.ewoudje.lasagna.kodec.createCodec
import java.util.function.BiFunction

class TexturedBiomeSource(val texture: ResourceLocation, val pallete: BiomePallete) : BiomeSource(pallete.possibleBiomes()) {

    override fun getNoiseBiome(i: Int, j: Int, k: Int, sampler: Climate.Sampler): Holder<Biome> {
        TODO("Not yet implemented")
    }

    override fun codec(): Codec<out BiomeSource> = CODEC

    override fun withSeed(seed: Long): BiomeSource = this

    companion object {
        val CODEC = createCodec<TexturedBiomeSource> {
            group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter { it.texture },
                BiomePallete.CODEC.fieldOf("pallete").forGetter { it.pallete }
            ).apply(this, ::TexturedBiomeSource)
        }
    }

    data class BiomePallete(private val map: Map<Int, Holder<Biome>>) {
        fun possibleBiomes() = map.values.stream()
        fun getBiome(i: Int) = map[i]

        companion object {
            val CODEC = Codec.unboundedMap(Codec.INT, Biome.CODEC).xmap({ BiomePallete(it) }) {it.map}
        }
    }
}