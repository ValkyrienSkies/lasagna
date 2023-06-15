package org.mashed.lasagna.fabric;

import kotlin.jvm.functions.Function0;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.api.registry.*;
import org.mashed.lasagna.api.registry.DeferredRegister;
import org.mashed.lasagna.api.registry.RegistrySupplier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeferredRegisterImpl<T> implements DeferredRegister<T>, AnonymousDeferredRegister<T> {
    private final String modId;
    private final Registry<T> registry;
    private final List<RegistrySupplier<T>> everMade = new ArrayList<>();

    public DeferredRegisterImpl(String modId, ResourceKey<Registry<T>> registry) {
        this.modId = modId;
        this.registry = (Registry<T>) Registry.REGISTRY.get(registry.location());
    }

    @NotNull
    @Override
    public <I extends T> RegistrySupplier<I> register(@NotNull String name, @NotNull Function0<? extends I> builder) {
        return register(new ResourceLocation(modId, name), builder);
    }

    @Override
    public void applyAll() {

    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        return everMade.iterator();
    }

    @NotNull
    @Override
    public <I extends T> RegistrySupplier<I> register(@NotNull ResourceLocation location, @NotNull Function0<? extends I> builder) {
        ResourceKey<T> key = ResourceKey.create(registry.key(), location);
        I result = Registry.register(registry, key, builder.invoke());

        RegistrySupplier<I> r = new RegistrySupplier<I>() {

            @NotNull
            @Override
            public Holder<I> holder() {
                return (Holder<I>) registry.getOrCreateHolder(key);
            }

            @NotNull
            @Override
            public String getName() {
                return location.getPath();
            }

            @Override
            public I get() {
                return result;
            }
        };

        everMade.add((RegistrySupplier<T>) r);
        return r;
    }
}
