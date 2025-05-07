package serie2.part3

class Node<T> (
    var value: T = Any() as T,
    var next: Node<T>? = null,
    var previous: Node<T>? = null) {
}

fun splitEvensAndOdds(list: Node<Int>) {
    if (list.next == list || list.next?.next == list) {
        return
    }

    var current = list.next

    while (current != list) {
        val next = current?.next

        if (current?.value?.rem(2) == 0) {
            current.previous?.next = current.next
            current.next?.previous = current.previous

            current.next = list.next
            current.previous = list
            list.next?.previous = current
            list.next = current
        }

        current = next
    }
}


fun <T> intersection(list1: Node<T>, list2: Node<T>, cmp: Comparator<T>): Node<T>? {
    if (list1.next == list1 || list2.next == list2) {
        return null
    }

    var n1 = list1.next
    var n2 = list2.next
    var result: Node<T>? = null
    var last: Node<T>? = null

    while (n1 != list1 && n2 != list2) {
        val comparetor = cmp.compare(n1!!.value, n2!!.value)

        when {
            comparetor == 0 -> {
                val nextPtr1 = n1.next
                val nextPtr2 = n2.next

                n1.previous!!.next = n1.next
                n1.next!!.previous = n1.previous

                n2.previous!!.next = n2.next
                n2.next!!.previous = n2.previous

                if (result == null) {
                    result = n1
                    last = n1
                    n1.previous = null  // Definir previous como null para o primeiro nó
                } else {
                    last!!.next = n1
                    n1.previous = last
                    last = n1
                }

                last!!.next = null

                n1 = nextPtr1
                n2 = nextPtr2
            }
            comparetor < 0 -> {
                n1 = n1.next
            }
            else -> {
                n2 = n2.next
            }
        }
    }

    return result
}
