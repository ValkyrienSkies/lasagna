package com.ewoudje.lasagna.fabric;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.fabricmc.api.ModInitializer;
import com.ewoudje.lasagna.LasagnaMod;
import com.ewoudje.lasagna.api.events.RegistryEvents;

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