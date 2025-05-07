package serie2.part1_2

class IntArrayList(private val capacity:Int) /* : Iterable <Int> */ {
    private var size = 0 // tamanho da lista
    private val values = IntArray(capacity)
    private var tail = 0 // tail é o índice do último elemento da lista
    private var head = 0 // head é o índice do primeiro elemento da lista

    fun append(x: Int): Boolean {
        if (size == capacity) {
            return false // List is full
        } else {
            values[tail] = x // Add the element
            tail = (tail + 1) % capacity // Wrap tail index
            size++ // Increment size
            return true // Return true
        }
    }

    fun get(n: Int): Int? {
        return if (n !in 0 until size) {
            null // If n is out of bounds, return null
        } else {
            values[(head + n) % capacity] // Calculate the correct index
        }
    }


    fun addToAll(x: Int) {
        for (i in 0 until size) {
            val index = (head + i) % capacity // Calculate the correct index
            values[index] += x
        }
    }

    fun remove(): Boolean {
        if (size == 0) return false // List is empty
        values[head] = 0 // Clear the element
        head = (head + 1) % capacity // Wrap head index
        size-- // Decrement size
        return true
    }

/*
    override fun iterator(): Iterator<Int> { // Opcional
    }
*/
}
