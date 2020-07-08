package de.nordgedanken.auto_hotkey.resolve.ref

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement
import de.nordgedanken.auto_hotkey.psi.ext.AHKReferenceElement

abstract class AHKReferenceCached<T : AHKReferenceElement>(
    element: T
) : AHKReferenceBase<T>(element) {

    protected abstract fun resolveInner(): List<AHKElement>

    final override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> =
            cachedMultiResolve().toTypedArray()

    final override fun multiResolve(): List<AHKElement> =
            cachedMultiResolve().mapNotNull { it.element as? AHKElement }

    private fun cachedMultiResolve(): List<PsiElementResolveResult> {
        return RsResolveCache.getInstance(element.project)
                .resolveWithCaching(element, cacheDependency, Resolver).orEmpty()
    }

    protected open val cacheDependency: ResolveCacheDependency get() = ResolveCacheDependency.LOCAL_AND_AHK_STRUCTURE

    private object Resolver : (AHKReferenceElement) -> List<PsiElementResolveResult> {
        override fun invoke(ref: AHKReferenceElement): List<PsiElementResolveResult> {
            return (ref.reference as AHKReferenceCached<*>).resolveInner().map { PsiElementResolveResult(it) }
        }
    }
}
