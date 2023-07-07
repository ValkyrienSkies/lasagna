package org.mashed.lasagna.fabric.services;

import com.mojang.serialization.Lifecycle;
import kotlin.jvm.functions.Function0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.mashed.lasagna.LasagnaMod;
import org.mashed.lasagna.networking.Serialization;
import org.mashed.lasagna.networking.ToClientPacketTarget;
import org.mashed.lasagna.services.LasagnaPlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.services.LasagnaPlatformHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.mashed.lasagna.fabric.LasagnaModFabric.*;

public class LasagnaPlatformHelperFabric implements LasagnaPlatformHelper {

    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Function0<ItemStack> stack) {
        return FabricItemGroupBuilder.build(id, stack::invoke);
    }

    @Environment(EnvType.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<WorldPreset>> worldPresetsRegistry() {
        return WORLD_PRESETS_REGISTRY;
    }

    @Environment(EnvType.CLIENT)
    @NotNull
    @Override
    public ResourceKey<Registry<DimensionSpecialEffects>> dimensionEffectsRegistry() {
        return DIMENSION_EFFECTS_REGISTRY;
    }

    @Override
    public void registerDataListener(@NotNull ResourceLocation id, @NotNull PreparableReloadListener listener) {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(
                    PreparationBarrier preparationBarrier,
                    ResourceManager resourceManager,
                    ProfilerFiller preparationsProfiler,
                    ProfilerFiller reloadProfiler,
                    Executor backgroundExecutor,
                    Executor gameExecutor
            ) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return id;
            }
        });
    }

    @Override
    public <T> void sendToClient(@NotNull Serialization<T> serialization, @NotNull ToClientPacketTarget target, T data) {

    }

    @Override
    public <T> void sendToServer(@NotNull Serialization<T> serialization, T data) {

    }
}
