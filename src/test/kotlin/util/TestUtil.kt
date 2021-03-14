package util

import com.intellij.openapi.util.JDOMUtil
import junit.framework.TestCase
import org.jdom.Element

const val PLUGIN_PACKAGE_PREFIX = "de/nordgedanken/auto_hotkey"

object TestUtil {
    val STACK_WALKER: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

    /**
     * Returns a string containing the content of the file at the given filename.
     * Note that the filepath is expected to be in the same package as the class
     * calling the method.
     */
    fun readResourceToString(filename: String): String {
        return javaClass.getResource("/${packagePath()}/$filename").readText()
    }

    /**
     * Calculates the package path based on where the method is called.
     *
     * Ex: if calling this from "de.nordgedanken.auto_hotkey.runconfig.model",
     * it will return "de/nordgedanken/auto_hotkey/runconfig/model".
     *
     * NOTE: Function MUST be inlined for the StackWalker to calculate the correct package path.
     * (Otherwise it would always just return TestUtil's package.)
     */
    inline fun packagePath(): String = STACK_WALKER.callerClass.packageName.replace('.', '/')
}

/**
 *  Looks for the specified annotation on the current test method.
 *  If not present, looks at the current class
 *
 *  (Copied from Rust plugin)
 */
inline fun <reified T : Annotation> TestCase.findAnnotationInstance(): T? =
    javaClass.getMethod(name).getAnnotation(T::class.java) ?: javaClass.getAnnotation(T::class.java)

fun Element.toXmlString() = JDOMUtil.writeElement(this)
