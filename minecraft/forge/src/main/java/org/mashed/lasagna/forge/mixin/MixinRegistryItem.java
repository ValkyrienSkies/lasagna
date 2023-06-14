package org.mashed.lasagna.forge.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.mashed.lasagna.api.registry.RegistryItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RegistryItem.class)
public interface MixinRegistryItem<T> extends IForgeRegistryEntry<T>, RegistryItem<T> {

    default T setRegistryName(ResourceLocation arg) {
        this.setId(arg);
        return (T) this;
    }

    @Nullable
    @Override
    default ResourceLocation getRegistryName() {
        return this.getId();
    }

    @Override
    default Class<T> getRegistryType() {
        return (Class<T>) this.getClass();
    }
}
