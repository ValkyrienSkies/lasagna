package me.ewoudje.lasagna

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

object LasagnaMod {
    const val MOD_ID = "lasagna"
    const val VERSION = "DEV"

    @JvmStatic
    fun init() {

    }

    @JvmStatic
    fun initClient() {

    }

    @JvmStatic
    fun registerClientCommands(dispatcher: CommandDispatcher<ClientSuggestionProvider>) {
        dispatcher.register(
            literal<ClientSuggestionProvider>("lasagna").then(
                literal<ClientSuggestionProvider>("version").executes {
                    Minecraft.gui.chat.addMessage(TextComponent("Lasagna version $VERSION"))
                    1
                })
        )
    }

    val String.resource: ResourceLocation get() = ResourceLocation(MOD_ID, this)
}