data class KotlinSourceFile(val relativePath: String, val content: String)

internal fun lineNumberForOffset(content: String, offset: Int): Int =
    content.substring(0, offset.coerceAtLeast(0)).count { it == '\n' } + 1
