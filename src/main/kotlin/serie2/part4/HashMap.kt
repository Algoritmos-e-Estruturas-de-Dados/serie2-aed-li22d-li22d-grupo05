package serie2.part4

class HashMap<K, V> (initialCapacity: Int = 4, private val loadFactor: Float = 0.75f) : MutableMap<K, V> {
    private class HashNode<K, V>(override val key: K, override var value: V,
                                 var next: HashNode<K, V>? = null
                                ): MutableMap.MutableEntry<K,V> {
        var hc = key.hashCode()
        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }
    }

    private var table: Array<HashNode<K, V>?> = arrayOfNulls(initialCapacity)
//O fator de carga é a razão entre o número de elementos presentes na tabela de dispersão e a sua dimensão.

// expandir tabela para o dobro quando o número de elementos multiplicado pelo fator de carga
// for igual ou superior à capacidade da tabela

    private fun expand() { // expandir tabela para o dobro quando o número de elementos multiplicado pelo fator de carga for igual ou superior à capacidade da tabela
        if (size * loadFactor >= table.size) {
            val newTable = arrayOfNulls<HashNode<K, V>>(table.size * 2)
            for (i in table.indices) {
                var node = table[i]
                while (node != null) {
                    val index = node.hc % newTable.size
                    val nextNode = node.next
                    node.next = newTable[index]
                    newTable[index] = node
                    node = nextNode
                }
            }
            table = newTable
        }
    }

    override val size: Int
        get() = table.count { it != null }
    override val capacity: Int
        get() = table.size

    override fun put(key: K, value: V): V? {
        expand()
        val index = key.hashCode() % table.size
        var node = table[index]
        while (node != null) {
            if (node.key == key) {
                val oldValue = node.value
                node.value = value
                return oldValue
            }
            node = node.next
        }
        table[index] = HashNode(key, value, table[index])
        return null
    }

    override fun get(key: K): V? {
        val index = key.hashCode() % table.size
        var node = table[index]
        while (node != null) {
            if (node.key == key) {
                return node.value
            }
            node = node.next
        }
        return null
    }

    override fun iterator(): Iterator<MutableMap.MutableEntry<K, V>> {
        return object : Iterator<MutableMap.MutableEntry<K, V>> {
            private var currentIndex = 0
            private var currentNode: HashNode<K, V>? = null

            override fun hasNext(): Boolean {
                if (currentNode != null && currentNode!!.next != null) {
                    return true
                }
                while (currentIndex < table.size) {
                    if (table[currentIndex] != null) {
                        currentNode = table[currentIndex]
                        currentIndex++
                        return true
                    }
                    currentIndex++
                }
                return false
            }

            override fun next(): MutableMap.MutableEntry<K, V> {
                if (currentNode == null) {
                    throw NoSuchElementException()
                }
                val entry = currentNode!!
                currentNode = currentNode!!.next
                return entry
            }
        }
    }

}
