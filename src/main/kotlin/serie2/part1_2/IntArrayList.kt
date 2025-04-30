package serie2.part1_2

class IntArrayList(private val capacity:Int) /* : Iterable <Int> */ {
    private var size = 0 // tamanho da lista
    private val values = IntArray(capacity)
    private var tail = 0 // tail é o índice do último elemento da lista
    private var head = 0 // head é o índice do primeiro elemento da lista

    fun append(x:Int):Boolean { // adiciona elemento x ao fim da lista
        if(size == capacity) return false // lista cheia
        values[tail] = x // adiciona o elemento
        tail += 1 // incrementa o tail
        size ++ // incrementa o tamanho
        return true // retorna true
    }

    fun get(n:Int):Int?  {
        return if ( n !in 0 until size) {
            null // se n não está no intervalo [0, size) retorna null
        } else {
            values[n] // retorna o elemento na posição n
        }
    }

    fun addToAll(x:Int)   {
        for (i in 0 until size) {
            values[i] += x // adiciona x a todos os elementos
        }

    }

    fun remove():Boolean {
        if (size == 0) return false // lista vazia
        values[head] = 0 // remove o elemento
        head += 1 // incrementa o head
        size --   // decrementa o tamanho
        return true
    }
/*
    override fun iterator(): Iterator<Int> { // Opcional


    }
 */
}