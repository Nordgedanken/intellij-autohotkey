package de.nordgedanken.auto_hotkey.resolve

import com.intellij.util.SmartList
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement

/**
 * ScopeEntry is some PsiElement visible in some code scope.
 *
 * [ScopeEntry] handles the two case:
 *   * aliases (that's why we need a [name] property)
 *   * lazy resolving of actual elements (that's why [element] can return `null`)
 */
interface ScopeEntry {
    val name: String
    val element: AHKElement?
}

/**
 * Return `true` to stop further processing,
 * return `false` to continue search
 */
typealias AHKResolveProcessor = (ScopeEntry) -> Boolean

fun collectResolveVariants(referenceName: String, f: (AHKResolveProcessor) -> Unit): List<AHKElement> {
    val result = SmartList<AHKElement>()
    f { e ->
        if (e.name == referenceName) {
            val element = e.element ?: return@f false
        }
        false
    }
    return result
}
