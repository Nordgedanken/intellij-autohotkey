package de.nordgedanken.auto_hotkey.psi.ext

import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.resolve.ref.AHKReference

/**
 * Provides basic methods for reference implementation ([de.nordgedanken.auto_hotkey.resolve.ref.AHKReferenceBase]).
 * This interface should not be used in any analysis.
 */
interface AHKReferenceElementBase : AHKElement {
    val referenceNameElement: PsiElement?

    @JvmDefault
    val referenceName: String? get() = referenceNameElement?.unescapedText
}
/**
 * Marks an element that optionally can have a reference.
 */
interface AHKReferenceElement : AHKReferenceElementBase {
    override fun getReference(): AHKReference?
}

/**
 * Marks an element that has a reference.
 */
interface AHKMandatoryReferenceElement : AHKReferenceElement {

    override val referenceNameElement: PsiElement

    @JvmDefault
    override val referenceName: String get() = referenceNameElement.unescapedText

    override fun getReference(): AHKReference
}
