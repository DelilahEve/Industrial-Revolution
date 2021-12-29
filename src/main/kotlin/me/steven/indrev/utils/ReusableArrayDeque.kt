package me.steven.indrev.utils

/**
 * Limited simple re-implementation of a reusable ArrayDeque
 * When polling, instead of reducing size and setting elements on the array to null, it will just increase the head
 * When finished, you can reset the head and start again
 * Used by IR's networks
 */
class ReusableArrayDeque<E : Comparable<E>>(elements: Collection<E>) : AbstractMutableList<E>() {
    private var head: Int = 0
    var elementData: Array<Any?>

    override var size: Int = 0
        private set

    init {
        elementData = elements.toTypedArray()
        size = elementData.size
        if (elementData.isEmpty()) elementData = emptyElementData
    }

    inline fun apply(transform: (Array<Any?>) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        transform(elementData)
    }

    override fun add(index: Int, element: E) = throw NotImplementedError()

    override fun removeAt(index: Int): E = throw NotImplementedError()

    override fun set(index: Int, element: E): E = throw NotImplementedError()

    override fun get(index: Int): E {
        checkElementIndex(index, size)

        return internalGet(internalIndex(index))
    }

    fun removeFirst(): E {
        if (isEmpty()) throw NoSuchElementException("ArrayDeque is empty.")

        val element = internalGet(head)
        head = incremented(head)
        return element
    }

    private fun incremented(index: Int): Int = index + 1

    private fun internalGet(internalIndex: Int): E {
        @Suppress("UNCHECKED_CAST")
        return elementData[internalIndex] as E
    }

    private fun internalIndex(index: Int): Int = positiveMod(head + index)

    private fun positiveMod(index: Int): Int = if (index >= elementData.size) index - elementData.size else index

    fun resetHead() {
        head = 0
    }

    override fun isEmpty(): Boolean = head >= size

    companion object {
        private val emptyElementData = emptyArray<Any?>()

        fun checkElementIndex(index: Int, size: Int) {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException("index: $index, size: $size")
            }
        }
    }
}