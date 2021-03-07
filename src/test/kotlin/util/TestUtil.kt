package util

import junit.framework.TestCase

const val PLUGIN_PACKAGE_PREFIX = "de/nordgedanken/auto_hotkey"

object TestUtil {
    private val STACK_WALKER: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

    /**
     * Returns a string containing the content of the file at the given filepath.
     * Note that the package path prefix is automatically calculated based on
     * where this method is called.
     *
     * Thus, if calling this from "de.nordgedanken.auto_hotkey.runconfig.model",
     * then "/de/nordgedanken/auto_hotkey/runconfig/model/" will automatically be
     * prefixed onto whatever filename you provide.
     */
    fun readResourceToString(filepath: String): String {
        val caller: Class<*> = STACK_WALKER.callerClass
        return javaClass.getResource("/${caller.packageName.replace('.', '/')}/$filepath").readText()
    }
}

/**
 *  Looks for the specified annotation on the current test method.
 *  If not present, looks at the current class
 *
 *  (Copied from Rust plugin)
 */
inline fun <reified T : Annotation> TestCase.findAnnotationInstance(): T? =
    javaClass.getMethod(name).getAnnotation(T::class.java) ?: javaClass.getAnnotation(T::class.java)
