package org.mashed.lasagna.fabric;

import com.mojang.brigadier.CommandDispatcher;
import kotlin.Unit;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.mashed.lasagna.LasagnaMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import org.mashed.lasagna.LasagnaMod;
import org.mashed.lasagna.api.events.RegistryEvents;
import org.mashed.lasagna.fabric.mixin.WorldPresetAccessor;
import org.mashed.lasagna.fabric.services.LasagnaPlatformHelperFabric;
import org.mashed.lasagna.mixin.DimensionSpecialEffectsAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LasagnaModFabric implements ModInitializer {
    private static final AtomicBoolean hasInitialized = new AtomicBoolean(false);
    public static final List<Registry> registries = new ArrayList<>();

    public static <T> Registry<T> track(Registry<T> registry) {
        registries.add(registry);
        return registry;
    }


    @Override
    public void onInitialize() {
        if (hasInitialized.getAndSet(true)) return;

        LasagnaMod.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LasagnaMod.registerServerCommands(dispatcher);
        });
    }
}