package org.mashed.lasagna.networking

import net.fabricmc.api.EnvType
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import org.mashed.lasagna.LasagnaMod.resource
import org.mashed.lasagna.Minecraft
import org.mashed.lasagna.api.events.RegistryEvents
import org.mashed.lasagna.api.registry.createUserRegistry
import org.mashed.lasagna.api.registry.getValue
import org.mashed.lasagna.services.LasagnaPlatformHelper
import org.mashed.lasagna.whenClient
import java.lang.IllegalArgumentException

object LasagnaNetworking {
    val REGISTRY_KEY = ResourceKey.createRegistryKey<Serialization<*>>("network_serialization".resource)
    private val registry by (Registry.REGISTRY as Registry<Registry<Serialization<*>>>).getOrCreateHolder(REGISTRY_KEY)
    private val classMapSerialization = mutableMapOf<Class<*>, Serialization<*>>()
    private val classMapServerListeners = mutableMapOf<Class<*>, Listeners<ServerPlayer, *>>()
    private val classMapClientListeners = mutableMapOf<Class<*>, Listeners<Minecraft, *>>()
    private val deferred = createUserRegistry(REGISTRY_KEY)

    @JvmStatic
    fun <T> register(serialization: Serialization<T>) {
        classMapSerialization[serialization.packetClass] = serialization
        deferred.register(
            serialization.id ?: throw IllegalArgumentException("serializations ID is null")
        ) { serialization }
    }

    @JvmStatic
    fun <T> packetServer(clazz: Class<T>, async: Boolean = false, call: (T, ServerPlayer) -> Unit) {
        val listeners = classMapServerListeners.getOrPut(clazz) { Listeners<ServerPlayer, T>() }
                as Listeners<ServerPlayer, T>

        listeners.listeners.add(async to call)
    }

    @JvmStatic
    fun <T> packetClient(clazz: Class<T>, async: Boolean = false, call: (T, Minecraft) -> Unit) {
        val listeners = classMapClientListeners.getOrPut(clazz) { Listeners<Minecraft, T>() }
                as Listeners<Minecraft, T>

        listeners.listeners.add(async to call)
    }

    @JvmStatic
    fun <T> send(target: PacketTarget, clazz: Class<T>, data: T) {
        val serialization = classMapSerialization[clazz] ?: throw IllegalArgumentException("Unknown serialization type: $clazz")
        serialization as Serialization<T>

        if (target is ToServerPacketTarget) {
            LasagnaPlatformHelper.sendToServer(serialization, data)
        } else {
            LasagnaPlatformHelper.sendToClient(serialization, target as ToClientPacketTarget, data)
        }
    }

    @JvmStatic
    fun <T> defineSerialization(id: ResourceKey<Serialization<*>>, clazz: Class<T>, encode: (T, FriendlyByteBuf) -> Unit, decode: (FriendlyByteBuf) -> T) {
        SerializationBuilder(id.location(), encode, clazz) decode decode
    }

    inline fun <reified T> packetServer(async: Boolean = false, noinline call: (T, ServerPlayer) -> Unit) = packetServer(T::class.java, async, call)
    inline fun <reified T> packetClient(async: Boolean = false, noinline call: (T, Minecraft) -> Unit) = packetClient(T::class.java, async, call)
    inline fun <reified T> send(target: PacketTarget, data: T) = send(target, T::class.java, data)

    fun <T> onPacketRecievedServer(packetClass: Class<T>): (T, ServerPlayer, (() -> Unit) -> Unit) -> Unit {
        val listener = (classMapServerListeners[packetClass] as Listeners<ServerPlayer, T>?)
            ?: throw IllegalArgumentException("Unknown packet type: $packetClass")

        return { msg, player, makeSync ->
            listener(msg, player, makeSync)
        }
    }

    fun <T> onPacketRecievedClient(packetClass: Class<T>): (T, (() -> Unit) -> Unit) -> Unit {
        val listener = (classMapClientListeners[packetClass] as Listeners<Minecraft, T>?)
            ?: throw IllegalArgumentException("Unknown packet type: $packetClass")

        return { msg, makeSync ->
            listener(msg, Minecraft, makeSync)
        }
    }

    init {
        RegistryEvents.onRegistriesComplete.register {
            classMapServerListeners.forEach { (clazz, _) ->
                val serialization = classMapSerialization[clazz] ?: throw IllegalArgumentException("Unknown serialization type: $clazz")
                LasagnaPlatformHelper.setupServerPacketHandler(serialization.id!!, serialization)
            }

            whenClient {
                classMapClientListeners.forEach { (clazz, _) ->
                    val serialization = classMapSerialization[clazz]
                        ?: throw IllegalArgumentException("Unknown serialization type: $clazz")
                    LasagnaPlatformHelper.setupClientPacketHandler(serialization.id!!, serialization)
                }
            }
        }
    }


    private data class Listeners<C, T> private constructor(val listeners: MutableList<Pair<Boolean, (T, C) -> Unit>>) {
        constructor() : this(mutableListOf())

        operator fun invoke(data: T, context: C, makeSync: (() -> Unit) -> Unit) {
            listeners.forEach { (async, listener) ->
                if (async) {
                    listener(data, context)
                } else {
                    makeSync {
                        listener(data, context)
                    }
                }
            }
        }
    }
}