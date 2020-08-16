package de.nordgedanken.auto_hotkey

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import de.nordgedanken.auto_hotkey.psi.AHKVariable
import de.nordgedanken.auto_hotkey.psi.ext.getKey
import de.nordgedanken.auto_hotkey.util.AhkIcons
import java.util.*


class AHKReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement?>(element, textRange), PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement?.project
        val properties: List<AHKVariable> = AHKUtil.findProperties(project, key)
        val results: MutableList<ResolveResult> = ArrayList()
        for (property in properties) {
            results.add(PsiElementResolveResult(property))
        }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        val project = myElement?.project
        val properties: List<AHKVariable> = AHKUtil.findProperties(project)
        val variants: MutableList<LookupElement> = ArrayList()
        for (property in properties) {
            if (property.getKey().isNotBlank() && property.getKey().isNotEmpty()) {
                variants.add(LookupElementBuilder
                        .create(property).withIcon(AhkIcons.FILE)
                        .withTypeText(property.containingFile.name)
                )
            }
        }
        return variants.toTypedArray()
    }

}
