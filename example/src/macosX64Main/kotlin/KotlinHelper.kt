import kotlin.math.PI

class KotlinHelper {
    fun hello(): String = "Hello world from Kotlin code"

    fun pi(): Double {
        return PI
    }

    fun printEvenNumbers() {
        val list = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        list.filter { it % 2 == 0 }
            .forEach { print("$it ") }
    }
}
