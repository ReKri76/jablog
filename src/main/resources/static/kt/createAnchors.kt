import kotlin.math.sqrt

fun main() {
    print("Введите a b c через пробел: ")
    val parts = readln().trim().split(Regex("\\s+"))
    if (parts.size != 3) {
        println("Ошибка: нужно ввести ровно 3 числа.")
        return
    }

    val a = parts[0].toDoubleOrNull()
    val b = parts[1].toDoubleOrNull()
    val c = parts[2].toDoubleOrNull()

    if (a == null || b == nul|| c == null) {
        println("Ошибка: введены не числа.")
        return
    }

    if (a == 0.0) {
        // bx + c = 0
        if (b == 0.0) {
            println(if (c == 0.0) "Бесконечно много решений." else "Решений нет.")
        } else {
            val x = -c / b
            println("Линейное уравнение, x = $x")
        }
        return
    }

    val d = b * b - 4 * a * c

    when {
        d > 0 -> {
            val sqrtD = sqrt(d)
            val x1 = (-b + sqrtD) / (2 * a)
            val x2 = (-b - sqrtD) / (2 * a)
            println("Два корня: x1 = $x1, x2 = $x2")
        }
        d == 0.0 -> {
            val x = -b / (2 * a)
            println("Один корень: x = $x")
        }
        else -> {
            println("Действительных корней нет.")
        }
    }
}