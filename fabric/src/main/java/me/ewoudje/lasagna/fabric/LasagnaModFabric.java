package me.ewoudje.lasagna.fabric;

import com.mojang.brigadier.CommandDispatcher;
import me.ewoudje.lasagna.LasagnaMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class LasagnaModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LasagnaMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            LasagnaMod.initClient();
            LasagnaMod.registerClientCommands((CommandDispatcher<ClientSuggestionProvider>) (Object) ClientCommandManager.DISPATCHER);
        }
    }
}