package de.nordgedanken.auto_hotkey.resolve.ref

import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.psi.AHKPatBinding
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement
import de.nordgedanken.auto_hotkey.resolve.collectResolveVariants


class AHKPatBindingReferenceImpl(
        element: AHKPatBinding
) : AHKReferenceCached<AHKPatBinding>(element) {

    override fun resolveInner(): List<AHKElement> =
            collectResolveVariants(element.referenceName) { false }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val target = resolve()
        return element.manager.areElementsEquivalent(target, element)
    }
}
