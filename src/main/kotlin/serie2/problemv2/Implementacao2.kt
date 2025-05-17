import java.io.File
import java.util.Scanner
import serie2.part4.AEDHashMap

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
    private val points = AEDHashMap<String, Point>()

    fun add(point: Point) {
        points.put(point.id, point)
    }

    fun getPoints(): List<Point> {
        val list = mutableListOf<Point>()
        for (entry in points) {
            val p = entry.value
            if (p != null) list.add(p)
        }
        return list
    }

    fun unionWith(other: Plane): List<Point> {
        val result = mutableListOf<Point>()
        val seen = mutableSetOf<Pair<Double, Double>>()

        fun addIfNew(p: Point) {
            val coord = p.x to p.y
            if (coord !in seen) {
                result.add(p)
                seen.add(coord)
            }
        }

        for (entry in this.points) {
            entry.value?.let { addIfNew(it) }
        }
        for (entry in other.points) {
            entry.value?.let { addIfNew(it) }
        }
        return result
    }

    fun intersectWith(other: Plane): List<Point> {
        val otherCoords = other.getPoints().map { it.x to it.y }.toSet()
        val result = mutableListOf<Point>()
        for (entry in this.points) {
            val p = entry.value ?: continue
            if ((p.x to p.y) in otherCoords) result.add(p)
        }
        return result
    }

    fun differenceFrom(other: Plane): List<Point> {
        val otherCoords = other.getPoints().map { it.x to it.y }.toSet()
        val result = mutableListOf<Point>()
        for (entry in this.points) {
            val p = entry.value ?: continue
            if ((p.x to p.y) !in otherCoords) result.add(p)
        }
        return result
    }
}

class ProcessPointsCollections {
    private var plane1 = Plane()
    private var plane2 = Plane()

    private fun loadPlanes(file1: String, file2: String) {
        plane1 = Plane()  // recria para "limpar"
        plane2 = Plane()
        loadPoints(plane1, file1)
        loadPoints(plane2, file2)
    }

    private fun loadPoints(plane: Plane, file: String) {
        val possiblePaths = listOf(
            File(file),
            File("src/main/resources/$file"),
            File("src/main/kotlin/$file"),
            File("src/$file")
        )

        println("\nProcurando arquivo '$file' em:")
        possiblePaths.forEach { f ->
            println("- ${f.absolutePath} (existe: ${f.exists()})")
        }

        val fileToRead = possiblePaths.find { it.exists() }
        if (fileToRead == null) {
            println("Erro: Arquivo '$file' não encontrado em locais comuns.")
            println("Diretório atual: ${System.getProperty("user.dir")}")
            println("Por favor, especifique o caminho completo ou mova o arquivo para uma das pastas acima.")
            return
        }

        try {
            var count = 0
            fileToRead.forEachLine { line ->
                val tokens = line.trim().split(Regex("\\s+"))
                if (tokens.isNotEmpty() && tokens[0] == "v" && tokens.size == 4) {
                    val id = tokens[1]
                    val x = tokens[2].toDoubleOrNull()
                    val y = tokens[3].toDoubleOrNull()
                    if (x != null && y != null) {
                        plane.add(Point(id, x, y))
                        count++
                    }
                }
            }
            println("Arquivo '${fileToRead.name}' processado com sucesso: $count pontos carregados.")
        } catch (e: Exception) {
            println("Erro ao ler arquivo '${fileToRead.name}': ${e.message}")
        }
    }

    private fun savePlane(points: List<Point>, file: String) {
        File(file).printWriter().use { out ->
            points.forEach { p ->
                out.println("${p.x} ${p.y}")
            }
        }
        println("Arquivo '$file' salvo com sucesso.")
    }

    private fun <T> measureTime(operationName: String, operation: () -> T): T {
        val start = System.nanoTime()
        val result = operation()
        val end = System.nanoTime()
        val durationMs = (end - start) / 1_000_000.0
        println("$operationName levou $durationMs ms")
        return result
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
                        println("Uso correto: load <arquivo1.co> <arquivo2.co>")
                        continue
                    }
                    loadPlanes(command[1], command[2])
                }
                "union" -> {
                    if (command.size != 2) {
                        println("Uso correto: union <output.co>")
                        continue
                    }
                    val unionPoints = measureTime("Union") { plane1.unionWith(plane2) }
                    savePlane(unionPoints, command[1])
                }
                "intersection" -> {
                    if (command.size != 2) {
                        println("Uso correto: intersection <output.co>")
                        continue
                    }
                    val intersectionPoints = measureTime("Intersection") { plane1.intersectWith(plane2) }
                    savePlane(intersectionPoints, command[1])
                }
                "difference" -> {
                    if (command.size != 2) {
                        println("Uso correto: difference <output.co>")
                        continue
                    }
                    val differencePoints = measureTime("Difference") { plane1.differenceFrom(plane2) }
                    savePlane(differencePoints, command[1])
                }
                "exit" -> return
                else -> println("Comando inválido.")
            }
        }
    }
}

fun main() {
    val process = ProcessPointsCollections()
    process.run()
}
