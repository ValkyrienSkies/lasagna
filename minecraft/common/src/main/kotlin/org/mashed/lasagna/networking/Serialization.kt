package org.mashed.lasagna.networking

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import net.minecraft.core.Registry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import org.mashed.lasagna.LasagnaMod.resource
import org.mashed.lasagna.api.registry.RegistryItem
import org.mashed.lasagna.api.registry.createUserRegistry
import org.mashed.lasagna.api.registry.getValue
import org.mashed.lasagna.services.LasagnaPlatformHelper
import org.mashed.lasagna.util.LengthByteBuf
import java.lang.IllegalArgumentException

class SerializationBuilder<T>(
    val id: ResourceLocation,
    val encode: (T, FriendlyByteBuf) -> Unit
) {
    operator fun invoke(decode: (FriendlyByteBuf) -> T) {
        val size = getSizeOfDecode(decode)
        LasagnaNetworking.register(Serialization(encode, decode, id, size))
    }
}

fun <T> defineSerialization(id: ResourceLocation, encode: (T, FriendlyByteBuf) -> Unit): SerializationBuilder<T> {
    return SerializationBuilder(id, encode)
}

class Serialization<T>(
    val encode: (T, FriendlyByteBuf) -> Unit,
    val decode: (FriendlyByteBuf) -> T,
    override var id: ResourceLocation? = null,
    val size: Int
): RegistryItem<Serialization<*>>

object LasagnaNetworking {
    val REGISTRY_KEY = ResourceKey.createRegistryKey<Serialization<*>>("network_serialization".resource)
    private val registry by (Registry.REGISTRY as Registry<Registry<Serialization<*>>>).getOrCreateHolder(REGISTRY_KEY)
    private val classMap = mutableMapOf<Class<*>, Serialization<*>>()
    private val deferred = createUserRegistry(REGISTRY_KEY)


    fun <T> register(serialization: Serialization<T>) =
        deferred.register(
            serialization.id ?: throw IllegalArgumentException("serializations ID is null")
        ) { serialization }

    fun <T> send(target: PacketTarget, clazz: Class<T>, data: T) {
        val serialization = classMap[clazz] ?: throw IllegalArgumentException("Unknown serialization type: $clazz")
        serialization as Serialization<T>

        if (target is ToServerPacketTarget) {
            LasagnaPlatformHelper.sendToServer(serialization, data)
        } else {
            LasagnaPlatformHelper.sendToClient(serialization, target as ToClientPacketTarget, data)
        }
    }
}

private fun<T> getSizeOfDecode(decode: (FriendlyByteBuf) -> T): Int {
    val buf = FriendlyByteBuf(LengthByteBuf())
    decode(buf)
    return buf.writerIndex()
}