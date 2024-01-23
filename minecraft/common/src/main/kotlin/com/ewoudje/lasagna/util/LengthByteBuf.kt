package com.ewoudje.lasagna.util

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.ByteBufUtil
import io.netty.util.ByteProcessor
import io.netty.util.CharsetUtil
import io.netty.util.internal.StringUtil
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.channels.GatheringByteChannel
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.Charset

class LengthByteBuf : ByteBuf() {
    private var length = 0

    override fun writeBoolean(p1: Boolean): ByteBuf { this.length++; return this }
    override fun writeByte(p1: Int): ByteBuf { this.length++; return this }
    override fun writeShort(p1: Int): ByteBuf { this.length += 2; return this }
    override fun writeShortLE(p1: Int): ByteBuf { this.length += 2; return this }
    override fun writeMedium(p1: Int): ByteBuf { this.length += 3; return this }
    override fun writeMediumLE(p1: Int): ByteBuf { this.length += 3; return this }
    override fun writeInt(p1: Int): ByteBuf { this.length += 4; return this }
    override fun writeIntLE(p1: Int): ByteBuf { this.length += 4; return this }
    override fun writeLong(p1: Long): ByteBuf { this.length += 8; return this }
    override fun writeLongLE(p1: Long): ByteBuf { this.length += 8; return this }
    override fun writeChar(p1: Int): ByteBuf { this.length += 2; return this }
    override fun writeFloat(p1: Float): ByteBuf { this.length += 4; return this }
    override fun writeDouble(p1: Double): ByteBuf { this.length += 8; return this }
    override fun writeBytes(p0: ByteBuf): ByteBuf { this.length += p0.readableBytes(); return this }
    override fun writeBytes(p0: ByteBuf, p1: Int): ByteBuf { this.length += p1; return this }
    override fun writeBytes(p0: ByteBuf, p1: Int, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun writeBytes(p0: ByteArray): ByteBuf { this.length += p0.size; return this }
    override fun writeBytes(p0: ByteArray, p1: Int, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun writeBytes(p0: ByteBuffer): ByteBuf { this.length += p0.remaining(); return this }
    override fun writeBytes(p0: InputStream, p1: Int): Int { this.length += p1; return p1 }
    override fun writeBytes(p0: ScatteringByteChannel, p1: Int): Int { this.length += p1; return p1 }
    override fun writeBytes(p0: FileChannel, p1: Long, p2: Int): Int = throw UnsupportedOperationException()
    override fun writeZero(p0: Int): ByteBuf = this
    override fun writeCharSequence(sequence: CharSequence, charset: Charset): Int {
        return if (charset == CharsetUtil.UTF_8) {
             ByteBufUtil.utf8MaxBytes(sequence)
        } else if (charset != CharsetUtil.US_ASCII && charset != CharsetUtil.ISO_8859_1) {
            val bytes: ByteArray = sequence.toString().toByteArray(charset)
            bytes.size
        } else {
            sequence.length
        }
    }
    
    override fun equals(other: Any?): Boolean =
        other === this || (other is LengthByteBuf && other.length == length)

    override fun hashCode(): Int = length

    override fun toString(cs: Charset): String = throw UnsupportedOperationException()
    override fun toString(p0: Int, p1: Int, p2: Charset): String = throw UnsupportedOperationException()

    override fun toString(): String = StringUtil.simpleClassName(this)

    override fun refCnt(): Int = 1

    override fun retain(p0: Int): ByteBuf = this
    override fun retain(): ByteBuf = this

    override fun touch(): ByteBuf = this
    override fun touch(p0: Any?): ByteBuf = this

    override fun release(): Boolean = false
    override fun release(p0: Int): Boolean = false

    override fun compareTo(other: ByteBuf): Int = throw UnsupportedOperationException()

    override fun capacity(): Int = Int.MAX_VALUE
    override fun capacity(p0: Int): ByteBuf = this
    override fun maxCapacity(): Int = Int.MAX_VALUE

    override fun alloc(): ByteBufAllocator = throw UnsupportedOperationException()

    override fun order(): ByteOrder = ByteOrder.BIG_ENDIAN
    override fun order(p0: ByteOrder): ByteBuf = this

    override fun unwrap(): ByteBuf = throw UnsupportedOperationException()

    override fun isDirect(): Boolean = true

    override fun isReadOnly(): Boolean = false
    override fun asReadOnly(): ByteBuf = this

    override fun readerIndex(): Int = throw UnsupportedOperationException()
    override fun readerIndex(p0: Int): ByteBuf = this

    override fun writerIndex(): Int = this.length
    override fun writerIndex(p0: Int): ByteBuf { this.length = p0; return this }

    override fun setIndex(p0: Int, p1: Int): ByteBuf = this

    override fun readableBytes(): Int = 0
    override fun writableBytes(): Int = Int.MAX_VALUE

    override fun maxWritableBytes(): Int = Int.MAX_VALUE

    override fun isReadable(): Boolean = false
    override fun isReadable(p0: Int): Boolean = false

    override fun isWritable(): Boolean = true
    override fun isWritable(p0: Int): Boolean = true
    
    override fun clear(): ByteBuf { this.length = 0; return this }

    override fun markReaderIndex(): ByteBuf = this
    override fun resetReaderIndex(): ByteBuf = throw UnsupportedOperationException()

    override fun markWriterIndex(): ByteBuf = this
    override fun resetWriterIndex(): ByteBuf = throw UnsupportedOperationException()
    
    override fun discardReadBytes(): ByteBuf = this
    override fun discardSomeReadBytes(): ByteBuf = this

    override fun ensureWritable(p0: Int): ByteBuf = this
    override fun ensureWritable(p0: Int, p1: Boolean): Int = 0

    override fun getBoolean(p0: Int): Boolean = throw UnsupportedOperationException()
    override fun getByte(p0: Int): Byte = throw UnsupportedOperationException()
    override fun getUnsignedByte(p0: Int): Short = throw UnsupportedOperationException()
    override fun getShort(p0: Int): Short = throw UnsupportedOperationException()
    override fun getShortLE(p0: Int): Short = throw UnsupportedOperationException()
    override fun getUnsignedShort(p0: Int): Int = throw UnsupportedOperationException()
    override fun getUnsignedShortLE(p0: Int): Int = throw UnsupportedOperationException()
    override fun getMedium(p0: Int): Int = throw UnsupportedOperationException()
    override fun getMediumLE(p0: Int): Int  = throw UnsupportedOperationException()
    override fun getUnsignedMedium(p0: Int): Int  = throw UnsupportedOperationException()
    override fun getUnsignedMediumLE(p0: Int): Int  = throw UnsupportedOperationException()
    override fun getInt(p0: Int): Int  = throw UnsupportedOperationException()
    override fun getIntLE(p0: Int): Int  = throw UnsupportedOperationException()
    override fun getUnsignedInt(p0: Int): Long = throw UnsupportedOperationException()
    override fun getUnsignedIntLE(p0: Int): Long = throw UnsupportedOperationException()
    override fun getLong(p0: Int): Long = throw UnsupportedOperationException()
    override fun getLongLE(p0: Int): Long = throw UnsupportedOperationException()
    override fun getChar(p0: Int): Char = throw UnsupportedOperationException()
    override fun getFloat(p0: Int): Float = throw UnsupportedOperationException()
    override fun getDouble(p0: Int): Double = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: ByteBuf): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: ByteBuf, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: ByteBuf, p2: Int, p3: Int): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: ByteArray): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: ByteArray, p2: Int, p3: Int): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: ByteBuffer): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: OutputStream?, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: GatheringByteChannel?, p2: Int): Int = throw UnsupportedOperationException()
    override fun getBytes(p0: Int, p1: FileChannel?, p2: Long, p3: Int): Int = throw UnsupportedOperationException()
    override fun getCharSequence(p0: Int, p1: Int, p2: Charset?): CharSequence = throw UnsupportedOperationException()
    override fun setBoolean(p0: Int, p1: Boolean): ByteBuf = throw UnsupportedOperationException()
    override fun setByte(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setShort(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setShortLE(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setMedium(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setMediumLE(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setInt(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setIntLE(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setLong(p0: Int, p1: Long): ByteBuf = throw UnsupportedOperationException()
    override fun setLongLE(p0: Int, p1: Long): ByteBuf = throw UnsupportedOperationException()
    override fun setChar(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setFloat(p0: Int, p1: Float): ByteBuf = throw UnsupportedOperationException()
    override fun setDouble(p0: Int, p1: Double): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ByteBuf?): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ByteBuf, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ByteBuf, p2: Int, p3: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ByteArray): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ByteArray, p2: Int, p3: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ByteBuffer): ByteBuf = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: InputStream, p2: Int): Int = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: ScatteringByteChannel, p2: Int): Int = throw UnsupportedOperationException()
    override fun setBytes(p0: Int, p1: FileChannel, p2: Long, p3: Int): Int = throw UnsupportedOperationException()
    override fun setZero(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun setCharSequence(p0: Int, p1: CharSequence, p2: Charset): Int = throw UnsupportedOperationException()
    override fun readBoolean(): Boolean = throw UnsupportedOperationException()
    override fun readByte(): Byte = throw UnsupportedOperationException()
    override fun readUnsignedByte(): Short = throw UnsupportedOperationException()
    override fun readShort(): Short = throw UnsupportedOperationException()
    override fun readShortLE(): Short = throw UnsupportedOperationException()
    override fun readUnsignedShort(): Int = throw UnsupportedOperationException()
    override fun readUnsignedShortLE(): Int = throw UnsupportedOperationException()
    override fun readMedium(): Int = throw UnsupportedOperationException()
    override fun readMediumLE(): Int = throw UnsupportedOperationException()
    override fun readUnsignedMedium(): Int = throw UnsupportedOperationException()
    override fun readUnsignedMediumLE(): Int = throw UnsupportedOperationException()
    override fun readInt(): Int = throw UnsupportedOperationException()
    override fun readIntLE(): Int = throw UnsupportedOperationException()
    override fun readUnsignedInt(): Long = throw UnsupportedOperationException()
    override fun readUnsignedIntLE(): Long = throw UnsupportedOperationException()
    override fun readLong(): Long = throw UnsupportedOperationException()
    override fun readLongLE(): Long = throw UnsupportedOperationException()
    override fun readChar(): Char = throw UnsupportedOperationException()
    override fun readFloat(): Float = throw UnsupportedOperationException()
    override fun readDouble(): Double = throw UnsupportedOperationException()
    override fun readBytes(p0: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: ByteBuf?): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: ByteBuf?, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: ByteBuf?, p1: Int, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: ByteArray?): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: ByteArray?, p1: Int, p2: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: ByteBuffer?): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: OutputStream?, p1: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readBytes(p0: GatheringByteChannel?, p1: Int): Int = throw UnsupportedOperationException()
    override fun readBytes(p0: FileChannel?, p1: Long, p2: Int): Int = throw UnsupportedOperationException()
    override fun readSlice(p0: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readRetainedSlice(p0: Int): ByteBuf = throw UnsupportedOperationException()
    override fun readCharSequence(p0: Int, p1: Charset?): CharSequence = throw UnsupportedOperationException()
    override fun skipBytes(p0: Int): ByteBuf = throw UnsupportedOperationException()
    override fun indexOf(p0: Int, p1: Int, p2: Byte): Int = throw UnsupportedOperationException()
    override fun bytesBefore(p0: Byte): Int = throw UnsupportedOperationException()
    override fun bytesBefore(p0: Int, p1: Byte): Int = throw UnsupportedOperationException()
    override fun bytesBefore(p0: Int, p1: Int, p2: Byte): Int = throw UnsupportedOperationException()
    override fun forEachByte(p0: ByteProcessor?): Int = throw UnsupportedOperationException()
    override fun forEachByte(p0: Int, p1: Int, p2: ByteProcessor?): Int = throw UnsupportedOperationException()
    override fun forEachByteDesc(p0: ByteProcessor?): Int = throw UnsupportedOperationException()
    override fun forEachByteDesc(p0: Int, p1: Int, p2: ByteProcessor?): Int = throw UnsupportedOperationException()

    override fun copy(): ByteBuf = LengthByteBuf().apply { this.length = this@LengthByteBuf.length }
    override fun copy(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()

    override fun slice(): ByteBuf = throw UnsupportedOperationException()
    override fun slice(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()

    override fun retainedSlice(): ByteBuf = throw UnsupportedOperationException()
    override fun retainedSlice(p0: Int, p1: Int): ByteBuf = throw UnsupportedOperationException()

    override fun duplicate(): ByteBuf = throw UnsupportedOperationException()
    override fun retainedDuplicate(): ByteBuf = throw UnsupportedOperationException()

    override fun nioBufferCount(): Int = 0
    override fun nioBuffer(): ByteBuffer = throw UnsupportedOperationException()
    override fun nioBuffer(p0: Int, p1: Int): ByteBuffer = throw UnsupportedOperationException()
    override fun internalNioBuffer(p0: Int, p1: Int): ByteBuffer = throw UnsupportedOperationException()

    override fun nioBuffers(): Array<ByteBuffer> = throw UnsupportedOperationException()
    override fun nioBuffers(p0: Int, p1: Int): Array<ByteBuffer> = throw UnsupportedOperationException()

    override fun hasArray(): Boolean = false
    override fun array(): ByteArray = throw UnsupportedOperationException()
    override fun arrayOffset(): Int = throw UnsupportedOperationException()

    override fun hasMemoryAddress(): Boolean = false
    override fun memoryAddress(): Long = throw UnsupportedOperationException()
}