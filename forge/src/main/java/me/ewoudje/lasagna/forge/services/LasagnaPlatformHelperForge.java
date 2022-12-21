package me.ewoudje.lasagna.forge.services;

import kotlin.jvm.functions.Function0;
import me.ewoudje.lasagna.services.LasagnaPlatformHelper;
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
}
