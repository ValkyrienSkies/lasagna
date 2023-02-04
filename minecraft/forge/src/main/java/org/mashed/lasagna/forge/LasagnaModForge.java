package org.mashed.lasagna.forge;


import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.mashed.lasagna.LasagnaMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.mashed.lasagna.LasagnaMod;
import org.mashed.lasagna.mixin.DimensionSpecialEffectsAccessor;

import java.util.function.Consumer;

@Mod(LasagnaMod.MOD_ID)
public class LasagnaModForge {
    boolean happendClientSetup = false;
    static IEventBus MOD_BUS;

    @OnlyIn(Dist.CLIENT)
    public static final ResourceKey<Registry<DimensionSpecialEffects>> DIMENSION_EFFECTS_REGISTRY = ResourceKey.createRegistryKey(
            new ResourceLocation(LasagnaMod.MOD_ID, "dimension_effects"));

    public LasagnaModForge() {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

//        MOD_BUS.addListener(this::onModelRegistry);
        MOD_BUS.addListener(this::clientSetup);
        MOD_BUS.addListener(this::createRegistries);
//        MOD_BUS.addListener(this::entityRenderers);

        LasagnaMod.init();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        LasagnaMod.initClient();
    }

    void createRegistries(final NewRegistryEvent event) {
        if (happendClientSetup)
            event.create(new RegistryBuilder()
                    .onBake((owner, stage) ->
                            owner.getKeys().forEach((Consumer<ResourceLocation>) key ->
                                    DimensionSpecialEffectsAccessor.getEFFECTS()
                                            .put(key, (DimensionSpecialEffects) owner.getValue(key))))
                    .setName(DIMENSION_EFFECTS_REGISTRY.location()));
    }
}