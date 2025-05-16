package serie2.problemv1import java.io.File
import java.util.Scanner

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

class Plane(
    private val points: MutableMap<String, Point> = mutableMapOf(),
) {
    fun clear() {
        points.clear()
    }

    fun add(point: Point) {
        points[point.id] = point
    }

    fun getPoints(): Set<Point> = points.values.toSet()

    fun unionWith(other: Plane): Set<Point> = this.getPoints().union(other.getPoints())

    fun intersectWith(other: Plane): Set<Point> = this.getPoints().intersect(other.getPoints())

    fun differenceFrom(other: Plane): Set<Point> = this.getPoints().subtract(other.getPoints())
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
        points: Set<Point>,
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