package de.nordgedanken.auto_hotkey.resolve.ref

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.psi.AHKPsiFactory
import de.nordgedanken.auto_hotkey.psi.AHKTypes.IDENTIFIER
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement
import de.nordgedanken.auto_hotkey.psi.ext.AHKReferenceElementBase

abstract class AHKReferenceBase<T : AHKReferenceElementBase>(
    element: T
) : PsiPolyVariantReferenceBase<T>(element),
        AHKReference {

    override fun resolve(): AHKElement? = super.resolve() as? AHKElement

    override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> =
            multiResolve().map { PsiElementResolveResult(it) }.toTypedArray()

    open val T.referenceAnchor: PsiElement? get() = referenceNameElement

    final override fun getRangeInElement(): TextRange = super.getRangeInElement()

    final override fun calculateDefaultRangeInElement(): TextRange {
        val anchor = element.referenceAnchor ?: return TextRange.EMPTY_RANGE
        check(anchor.parent === element)
        return TextRange.from(anchor.startOffsetInParent, anchor.textLength)
    }

    override fun handleElementRename(newName: String): PsiElement {
        val referenceNameElement = element.referenceNameElement
        if (referenceNameElement != null) {
            doRename(referenceNameElement, newName)
        }
        return element
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun equals(other: Any?): Boolean = other is AHKReferenceBase<*> && element === other.element

    override fun hashCode(): Int = element.hashCode()

    companion object {
        @JvmStatic protected fun doRename(identifier: PsiElement, newName: String) {
            val factory = AHKPsiFactory(identifier.project)
            val newId = when (identifier.elementType) {
                else -> error("Unsupported identifier type for `$newName` (${identifier.elementType})")
            }
            identifier.replace(newId)
        }
    }
}

fun isValidAHKVariableIdentifier(name: String): Boolean = getLexerType(name) == IDENTIFIER

private fun getLexerType(text: String): IElementType? {
    val lexer = AHKLexer()
    lexer.start(text)
    return if (lexer.tokenEnd == text.length) lexer.tokenType else null
}
