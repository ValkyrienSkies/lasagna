package com.ewoudje.lasagna.api.events

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider

object RegistryEvents {

    val onTagsLoaded = makeEvent<Unit>() // Doesn't get executed TODO

    val onRegistriesComplete = makeEvent<Unit>()
    val onClientCommandRegister = makeEvent<CommandDispatcher<SharedSuggestionProvider>>()
    val onServerCommandRegister = makeEvent<CommandDispatcher<CommandSourceStack>>()
}