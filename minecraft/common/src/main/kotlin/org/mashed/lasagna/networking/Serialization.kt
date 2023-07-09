package org.mashed.lasagna.networking

import net.minecraft.network.FriendlyByteBuf
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
    val encode: (T, FriendlyByteBuf) -> Unit,
    val packetClass: Class<T>
) {
    infix fun decode(decode: (FriendlyByteBuf) -> T) {
        //val size = getSizeOfDecode(decode)
        LasagnaNetworking.register(Serialization(encode, decode, id,/*size,*/ packetClass))
    }
}

inline fun <reified T> defineSerialization(id: ResourceLocation, noinline encode: (T, FriendlyByteBuf) -> Unit): SerializationBuilder<T> {
    return SerializationBuilder(id, encode, T::class.java)
}

class Serialization<T>(
    val encode: (T, FriendlyByteBuf) -> Unit,
    val decode: (FriendlyByteBuf) -> T,
    override var id: ResourceLocation? = null,
//    val size: Int,
    val packetClass: Class<T>
): RegistryItem<Serialization<*>>


private fun<T> getSizeOfDecode(decode: (FriendlyByteBuf) -> T): Int {
    val buf = FriendlyByteBuf(LengthByteBuf())
    decode(buf)
    return buf.writerIndex()
}