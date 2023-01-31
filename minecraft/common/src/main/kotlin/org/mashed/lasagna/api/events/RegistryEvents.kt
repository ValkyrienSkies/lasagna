package org.mashed.lasagna.api.events

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.multiplayer.ClientSuggestionProvider
import net.minecraft.commands.CommandSourceStack

object RegistryEvents {

    val onTagsLoaded = makeEvent<Unit>() // Doesn't get executed TODO

    val onRegistriesComplete = makeEvent<Unit>()
    val onClientCommandRegister = makeEvent<CommandDispatcher<ClientSuggestionProvider>>()
    val onServerCommandRegister = makeEvent<CommandDispatcher<CommandSourceStack>>()
}