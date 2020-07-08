package de.nordgedanken.auto_hotkey

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Condition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet
import com.intellij.util.ProcessingContext
import de.nordgedanken.auto_hotkey.psi.AHKLitExpr
import de.nordgedanken.auto_hotkey.psi.AHKLiteralKind
import de.nordgedanken.auto_hotkey.psi.kind

class AHKReferenceContributor : PsiReferenceContributor() {
    private val log: Logger = Logger.getInstance("#de.nordgedanken.auto_hotkey.AHKReferenceContributor")
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<out FileReference> {
                    val stringLiteral = (element as? AHKLitExpr)?.kind as? AHKLiteralKind.String ?: return emptyArray()
                    if (stringLiteral.isByte) return emptyArray()
                    val startOffset = stringLiteral.offsets.value?.startOffset ?: return emptyArray()
                    val fs = element.containingFile.originalFile.virtualFile.fileSystem
                    return AHKLiteralFileReferenceSet(stringLiteral.value ?: "", element, startOffset, fs.isCaseSensitive).allReferences
                }
            }
        )
    }
}

private class AHKLiteralFileReferenceSet(
    str: String,
    element: AHKLitExpr,
    startOffset: Int,
    isCaseSensitive: Boolean
) : FileReferenceSet(str, element, startOffset, null, isCaseSensitive) {

    override fun getDefaultContexts(): Collection<PsiFileSystemItem> {
        return when (val parent = element.parent) {
            else -> emptyList()
        }
    }

    override fun getReferenceCompletionFilter(): Condition<PsiFileSystemItem> {
        return when (element.parent) {
            else -> super.getReferenceCompletionFilter()
        }
    }
}
