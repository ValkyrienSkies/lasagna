package org.mashed.lasagna.forge;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.mashed.lasagna.LasagnaMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.mashed.lasagna.LasagnaMod;
import org.mashed.lasagna.mixin.DimensionSpecialEffectsAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod(LasagnaMod.MOD_ID)
public class LasagnaModForge {
    private static final List<Pair<Class<?>, ResourceKey<?>>> registries = new ArrayList<>();
    private static final List<PreparableReloadListener> reloadListeners = new ArrayList<>();
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKETS = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LasagnaMod.MOD_ID, "packets"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    boolean happendClientSetup = false;
    public static IEventBus MOD_BUS;

    @OnlyIn(Dist.CLIENT)
    public static final ResourceKey<Registry<DimensionSpecialEffects>> DIMENSION_EFFECTS_REGISTRY = ResourceKey.createRegistryKey(
            new ResourceLocation(LasagnaMod.MOD_ID, "dimension_effects"));

    public static <T> void makeRegistry(Class<T> clazz, ResourceKey<Registry<T>> registry) {
        registries.add(Pair.of(clazz, registry));
    }

    public LasagnaModForge() {
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

//        MOD_BUS.addListener(this::onModelRegistry);
        MOD_BUS.addListener(this::clientSetup);
        MOD_BUS.addListener(this::createRegistries);
//        MOD_BUS.addListener(this::entityRenderers);

        MinecraftForge.EVENT_BUS.addListener(this::createResourceListeners);
        MinecraftForge.EVENT_BUS.addListener(this::registerServerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::registerClientCommands);

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

        registries.forEach((pair) -> {
            event.create(new RegistryBuilder()
                    .setName(pair.getSecond().location())
                    .setType(pair.getFirst()));
        });
    }

    void createResourceListeners(final AddReloadListenerEvent event) {
        reloadListeners.forEach(event::addListener);
    }

    void registerServerCommands(final RegisterCommandsEvent event) {
        LasagnaMod.registerServerCommands(event.getDispatcher());
    }

    void registerClientCommands(final RegisterClientCommandsEvent event) {
        LasagnaMod.registerClientCommands((CommandDispatcher<SharedSuggestionProvider>) (Object) event.getDispatcher());
    }

    public static void addReloadListener(PreparableReloadListener listener) {
        reloadListeners.add(listener);
    }
}