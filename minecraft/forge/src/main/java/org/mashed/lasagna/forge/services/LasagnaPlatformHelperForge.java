package org.mashed.lasagna.forge.services;

import kotlin.jvm.functions.Function0;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.registries.ForgeRegistries;
import org.mashed.lasagna.forge.LasagnaModForge;
import org.mashed.lasagna.services.LasagnaPlatformHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LasagnaPlatformHelperForge implements LasagnaPlatformHelper {
    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Function0<ItemStack> stack) {
        return new CreativeModeTab(id.toString()) {
            @Override
            public ItemStack makeIcon() {
                return stack.invoke();
            }

            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("itemGroup." + String.format("%s.%s", id.getNamespace(), id.getPath()));
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<WorldPreset>> worldPresetsRegistry() {
        return (ResourceKey) ForgeRegistries.Keys.WORLD_TYPES;
    }

    @OnlyIn(Dist.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<DimensionSpecialEffects>> dimensionEffectsRegistry() {
        return LasagnaModForge.DIMENSION_EFFECTS_REGISTRY;
    }
}
