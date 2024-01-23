package com.ewoudje.lasagna.util

interface ImmLinkedList<T> {
    val value: T
    val next: ImmLinkedList<T>?
    fun add(t: T): ImmLinkedList<T> = NonEmptyLinkedList(t, this)
    fun toArray(): Array<T> {
        var list: ImmLinkedList<T>? = this
        var size = 0
        while (list != null && list != EmptyLinkedList) {
            list = list.next
            size++
        }

        val array = arrayOfNulls<Any>(size)

        list = this
        var index = 0
        while (list != null && list != EmptyLinkedList) {
            array[index++] = list.value
            list = list.next
        }

        return array as Array<T>
    }

    companion object {
        operator fun <T> invoke(): ImmLinkedList<T> = EmptyLinkedList as ImmLinkedList<T>
    }
}

private class NonEmptyLinkedList<T>(override val value: T, override val next: ImmLinkedList<T>) : ImmLinkedList<T>
private object EmptyLinkedList : ImmLinkedList<Any?> {
    override val value: Nothing get() = throw NoSuchElementException()
    override val next: ImmLinkedList<Any?>? get() = null
}