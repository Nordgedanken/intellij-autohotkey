package util

const val PACKAGE_PREFIX = "/de/nordgedanken/auto_hotkey"

object TestUtil {
    fun readResourceToString(path: String): String {
        return javaClass.getResource("$PACKAGE_PREFIX/$path").readText()
    }
}
