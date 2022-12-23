package org.mashed.lasagna.kodec

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.commands.arguments.UuidArgument
import org.mashed.lasagna.util.ImmLinkedList
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.jvmErasure

val <T : Any> KClass<T>.codec: Codec<T> get() = Kodec.getCodecForClass(this) as Codec<T>

object Kodec {
    private val codecCache = mutableMapOf<KClass<*>, Codec<*>>()

    fun getCodecForClass(clazz: KClass<*>): Codec<*> = codecCache.getOrPut(clazz) { buildCodec(clazz) }

    fun <T : Any> buildCodec(kClass: KClass<T>): Codec<T> {
        if (kClass.isData) return buildDataCodec(kClass)

        val codecField = kClass.java.declaredFields.filter { Modifier.isStatic(it.modifiers) }.firstOrNull { it.name == "CODEC" }
        if (codecField != null)
            return codecField.get(null) as Codec<T>

        throw IllegalArgumentException("Cannot build codec for $kClass")
    }
    private fun <T : Any> buildDataCodec(kClass: KClass<T>): Codec<T> {
        assert(kClass.constructors.size == 1) { "Only single-arg constructors are supported" }
        val constructor = kClass.constructors.first()

        return RecordCodecBuilder.create { instance ->
            var build = instance.point(ImmLinkedList<Any?>())

            kClass.declaredMemberProperties.forEach { prop ->
                build = instance.ap(instance.map({i: Any? -> Function { l: ImmLinkedList<Any?> -> l.add(i)}},
                    (prop.returnType.jvmErasure as KClass<Any>).codec.fieldOf(prop.name).forGetter { prop.get(it) }), build)
            }


            val end = instance.point(Function<ImmLinkedList<Any?>, T> { t -> constructor.call(*t.toArray()) })
            instance.ap(end, build)
        }
    }

    private inline fun <reified T: Any> defineCodec(codec: Codec<T>) = codecCache.put(T::class, codec)

    init {
        defineCodec(Codec.BOOL)

        defineCodec(Codec.BYTE)
        defineCodec(Codec.SHORT)
        defineCodec(Codec.INT)
        defineCodec(Codec.LONG)

        defineCodec(Codec.FLOAT)
        defineCodec(Codec.DOUBLE)

        defineCodec(Codec.INT_STREAM)
        defineCodec(Codec.LONG_STREAM)
        defineCodec(Codec.BYTE_BUFFER)

        defineCodec(Codec.STRING)

        // TODO more codecs
        // we should add all codecs spread over mc codebase
    }
}
