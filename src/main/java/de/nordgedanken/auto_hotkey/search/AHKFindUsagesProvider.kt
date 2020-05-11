package de.nordgedanken.auto_hotkey.search

import com.intellij.lang.HelpID
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import de.nordgedanken.auto_hotkey.psi.ext.AHKNamedElement

class AHKFindUsagesProvider : FindUsagesProvider {
    // XXX: must return new instance of WordScanner here, because it is not thread safe
    override fun getWordsScanner() = AHKWordScanner()

    override fun canFindUsagesFor(element: PsiElement) =
            element is AHKNamedElement

    override fun getHelpId(element: PsiElement) = HelpID.FIND_OTHER_USAGES

    override fun getType(element: PsiElement) = ""
    override fun getDescriptiveName(element: PsiElement) = ""
    override fun getNodeText(element: PsiElement, useFullName: Boolean) = ""
}
