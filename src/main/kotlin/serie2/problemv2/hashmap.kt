import serie2.part4.HashMap

data class Point(
    val id: String,
    val x: Double,
    val y: Double,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Point) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()
}


class Plane {
    private val points = HashMap<String, Point>()

    fun clear() =
        points.run {
            for (entry in this) {
                put(entry.key, null as Point) // remove all by overwriting with null
            }
        }

    fun add(point: Point) {
        points.put(point.id, point)
    }

    fun allPoints(): List<Point> {
        val list = mutableListOf<Point>()
        for (entry in points) {
            entry.value?.let { list.add(it) }
        }
        return list
    }

    fun unionWith(other: Plane): List<Point> {
        val result = mutableListOf<Point>()
        val seen = mutableListOf<Pair<Double, Double>>()

        fun addIfNew(point: Point) {
            val coord = point.x to point.y
            if (!seen.contains(coord)) {
                result.add(point)
                seen.add(coord)
            }
        }

        for (entry in this.points) entry.value?.let { addIfNew(it) }
        for (entry in other.points) entry.value?.let { addIfNew(it) }

        return result
    }

    fun intersectWith(other: Plane): List<Point> {
        val result = mutableListOf<Point>()
        val otherCoords = other.allPoints().map { it.x to it.y }

        for (entry in this.points) {
            val point = entry.value ?: continue
            if ((point.x to point.y) in otherCoords) {
                result.add(point)
            }
        }

        return result
    }

    fun differenceFrom(other: Plane): List<Point> {
        val result = mutableListOf<Point>()
        val otherCoords = other.allPoints().map { it.x to it.y }

        for (entry in this.points) {
            val point = entry.value ?: continue
            if ((point.x to point.y) !in otherCoords) {
                result.add(point)
            }
        }

        return result
    }
}

class ProcessPointsCollections {
    private val plane1 = Plane()
    private val plane2 = Plane()

    private fun loadPlanes(
        file1: String,
        file2: String,
    ) {
        plane1.clear()
        plane2.clear()
        loadPoints(plane1, file1)
        loadPoints(plane2, file2)
    }

    private fun loadPoints(
        plane: Plane,
        file: String,
    ) {
        File(file).forEachLine { line ->
            val tokens = line.trim().split(Regex("\\s+"))
            if (tokens.isNotEmpty() && tokens[0] == "v" && tokens.size == 4) {
                val id = tokens[1]
                val x = tokens[2].toDoubleOrNull()
                val y = tokens[3].toDoubleOrNull()

                if (x != null && y != null) {
                    plane.add(Point(id, x, y))
                }
            }
        }
    }

    private fun savePlane(
        points: List<Point>,
        file: String,
    ) {
        File(file).printWriter().use { out ->
            points.forEach { point ->
                out.println("${point.x} ${point.y}")
            }
        }
    }

    fun run() {
        val scanner = Scanner(System.`in`)

        while (true) {
            println("\nIntroduza o comando:")
            print("> ")
            val command = scanner.nextLine().trim().split(Regex("\\s+"))

            when (command.firstOrNull()) {
                "load" -> {
                    if (command.size != 3) {
                        println("Comando inválido. Uso correto: load <document1.co> <document2.co>")
                        continue
                    }
                    loadPlanes(command[1], command[2])
                }

                "union" -> {
                    if (command.size != 2) {
                        println("Comando inválido. Uso correto: union <output.co>")
                        continue
                    }
                    savePlane(plane1.unionWith(plane2), command[1])
                }

                "intersection" -> {
                    if (command.size != 2) {
                        println("Comando inválido. Uso correto: intersection <output.co>")
                        continue
                    }
                    savePlane(plane1.intersectWith(plane2), command[1])
                }

                "difference" -> {
                    if (command.size != 2) {
                        println("Comando inválido. Uso correto: difference <output.co>")
                        continue
                    }
                    savePlane(plane1.differenceFrom(plane2), command[1])
                }

                "exit" -> return
                else -> println("Comando inválido.")
            }
        }
    }
}

fun main() {
    val pct = ProcessPointsCollections()
    pct.run()
}
