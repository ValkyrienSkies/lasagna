package com.ewoudje.lasagna.networking

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import com.ewoudje.lasagna.LasagnaMod.resource
import com.ewoudje.lasagna.api.registry.RegistryItem
import com.ewoudje.lasagna.api.registry.createUserRegistry
import com.ewoudje.lasagna.api.registry.getValue
import com.ewoudje.lasagna.services.LasagnaPlatformHelper
import com.ewoudje.lasagna.util.LengthByteBuf
import java.lang.IllegalArgumentException

private var iid = 0
class SerializationBuilder<T>(
    val id: ResourceLocation,
    val encode: (T, FriendlyByteBuf) -> Unit,
    val packetClass: Class<T>
) {
    infix fun decode(decode: (FriendlyByteBuf) -> T) {
        //val size = getSizeOfDecode(decode)
        LasagnaNetworking.register(Serialization(encode, decode, id,/*size,*/ iid++, packetClass))
    }
}

inline fun <reified T> defineSerialization(id: ResourceLocation, noinline encode: (T, FriendlyByteBuf) -> Unit): SerializationBuilder<T> {
    return SerializationBuilder(id, encode, T::class.java)
}

class Serialization<T>(
    val encode: (T, FriendlyByteBuf) -> Unit,
    val decode: (FriendlyByteBuf) -> T,
    override var id: ResourceLocation? = null,
    val iid: Int,
//    val size: Int,
    val packetClass: Class<T>
): RegistryItem<Serialization<*>>
