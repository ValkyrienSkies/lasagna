package org.mashed.lasagna.kodec

import com.mojang.datafixers.kinds.App
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

fun <O> createCodec(builder: RecordCodecBuilder.Instance<O>.() -> App<RecordCodecBuilder.Mu<O>, O>): Codec<O> =
    RecordCodecBuilder.create(builder)