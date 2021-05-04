package util

import com.intellij.openapi.util.JDOMUtil
import junit.framework.TestCase
import org.jdom.Element
import org.jdom.input.SAXBuilder
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import java.io.StringReader
import java.net.URL

object TestUtil {
    val STACK_WALKER: StackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
    val XML_PARSER = SAXBuilder()

    /**
     * Returns a reference to the resource file at the given filename.
     * Note that the filepath is expected to be in the same package as the class
     * calling the method.
     */
    fun getResourceFile(filename: String): URL {
        return javaClass.getResource("/${packagePath()}/$filename")!!
    }

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
    @Suppress("NOTHING_TO_INLINE")
    inline fun packagePath(): String = STACK_WALKER.callerClass.packageName.replace('.', '/')

    /**
     * Parses the given xml file into a JDOM Element. The file must be in src/test/resources/{packagePath}/.
     * This function must be inline in order to get the correct package path of the calling method.
     *
     * @param filename The xml file's name without the extension
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun parseXmlFileToElement(filename: String): Element {
        return XML_PARSER.build(StringReader(readResourceToString("$filename.xml"))).rootElement
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

fun Element.toXmlString() = JDOMUtil.writeElement(this)

fun Element.toCompactXmlString() = XMLOutputter(Format.getCompactFormat()).outputString(this)
