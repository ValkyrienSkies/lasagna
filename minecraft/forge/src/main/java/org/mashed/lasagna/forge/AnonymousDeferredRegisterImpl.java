package org.mashed.lasagna.forge;

import kotlin.jvm.functions.Function0;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.api.registry.AnonymousDeferredRegister;
import org.mashed.lasagna.api.registry.RegistrySupplier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class AnonymousDeferredRegisterImpl<T extends IForgeRegistryEntry<T>> implements AnonymousDeferredRegister<T> {
    private final ResourceKey<Registry<T>> registry;
    private final List<RegistrySupplier<T>> entries = new ArrayList<>();
    public AnonymousDeferredRegisterImpl(@NotNull ResourceKey<Registry<T>> registry) {
        this.registry = registry;
    }


    @NotNull
    @Override
    public <I extends T> RegistrySupplier<I> register(@NotNull ResourceLocation name, @NotNull Function0<? extends I> builder) {
        var supplier = new RegistrySupplierImpl<>(() -> {
            var result = builder.invoke();
            result.setRegistryName(name);
            return result;
        });

        return (RegistrySupplier<I>) supplier;
    }

    @Override
    public void applyAll() {
        LasagnaModForge.MOD_BUS.addListener(this::registerAll);
    }

    private void registerAll(RegistryEvent.Register<T> event) {
        if (!event.getName().equals(registry.location())) return;

        for (RegistrySupplier<T> entry : entries) {
            event.getRegistry().register(entry.get());
        }
    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        return entries.iterator();
    }

    private static class RegistrySupplierImpl<T extends IForgeRegistryEntry<T>> implements RegistrySupplier<T> {
        private final T value;

        private RegistrySupplierImpl(Supplier<T> value) {
            this.value = value.get();
        }

        @NotNull
        @Override
        public String getName() {
            return Objects.requireNonNull(value.getRegistryName()).getPath();
        }

        @Override
        public T get() {
            return value;
        }

        @NotNull
        @Override
        public Holder<T> holder() {
            return Holder.direct(value);
        }
    }
}
