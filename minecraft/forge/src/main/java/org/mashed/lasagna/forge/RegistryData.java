package org.mashed.lasagna.forge;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record RegistryData<T>(Class<T> clazz, ResourceLocation key, @Nullable Codec<T> codec, @Nullable Codec<T> networkCodec) {}