package org.mashed.lasagna.forge.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(DimensionSpecialEffects.class)
public class MixinDimensionSpecialEffects implements IForgeRegistryEntry<DimensionSpecialEffects> {

    @Shadow @Final private static Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS;

    @Override
    public DimensionSpecialEffects setRegistryName(ResourceLocation arg) {
        return EFFECTS.put(arg, (DimensionSpecialEffects) (Object) this);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return EFFECTS.entrySet().stream().filter((entry) -> (Object) entry.getValue() == this)
                .findFirst().map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public Class<DimensionSpecialEffects> getRegistryType() {
        return DimensionSpecialEffects.class;
    }
}
