package org.mashed.lasagna.fabric;

import com.mojang.brigadier.CommandDispatcher;
import kotlin.Unit;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.DimensionSpecialEffects;
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

import java.util.concurrent.atomic.AtomicBoolean;

public class LasagnaModFabric implements ModInitializer {
    private final AtomicBoolean hasInitialized = new AtomicBoolean(false);
    public static final ResourceKey<Registry<WorldPreset>> WORLD_PRESETS_REGISTRY = ResourceKey.createRegistryKey(
            new ResourceLocation(LasagnaMod.MOD_ID, "world_presets"));
    public static final ResourceKey<Registry<DimensionSpecialEffects>> DIMENSION_EFFECTS_REGISTRY = ResourceKey.createRegistryKey(
            new ResourceLocation(LasagnaMod.MOD_ID, "dimension_effects"));

    public static final Registry<WorldPreset> WORLD_PRESETS =
            FabricRegistryBuilder.createSimple(WorldPreset.class, WORLD_PRESETS_REGISTRY.location()).buildAndRegister();

    public static final Registry<DimensionSpecialEffects> DIMENSION_EFFECTS =
            FabricRegistryBuilder.createSimple(DimensionSpecialEffects.class, DIMENSION_EFFECTS_REGISTRY.location()).buildAndRegister();


    @Override
    public void onInitialize() {
        if (hasInitialized.getAndSet(true)) return;

        LasagnaMod.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LasagnaMod.registerServerCommands(dispatcher);
        });
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {
        private final AtomicBoolean hasClientInitialized = new AtomicBoolean(false);

        @Override
        public void onInitializeClient() {
            if (hasClientInitialized.getAndSet(true)) return;

            LasagnaMod.initClient();
            LasagnaMod.registerClientCommands((CommandDispatcher<ClientSuggestionProvider>) (Object) ClientCommandManager.DISPATCHER);

            RegistryEvents.INSTANCE.getOnRegistriesComplete().register(_u -> {
                WORLD_PRESETS.freeze();
                DIMENSION_EFFECTS.freeze();

                WORLD_PRESETS.forEach((preset) -> {
                    WorldPresetAccessor.getPresets().add(preset);
                });

                DIMENSION_EFFECTS.forEach((effect) -> {
                    DimensionSpecialEffectsAccessor.getEFFECTS().put(DIMENSION_EFFECTS.getKey(effect), effect);
                });

                return Unit.INSTANCE;
            });
        }
    }
}