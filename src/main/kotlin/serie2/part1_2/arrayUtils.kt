package serie2.part1_2
import kotlin.random.Random

fun minimum(maxHeap: Array<Int>, heapSize: Int): Int {
    if (heapSize == 0) throw Exception("Heap está avzio")
    if (heapSize == 1) return maxHeap[0]

    var min = maxHeap[0]

    for (i in 1 until heapSize) {
        if (maxHeap[i] < min) {
            min = maxHeap[i]
        }
    }
    
    return min
}
