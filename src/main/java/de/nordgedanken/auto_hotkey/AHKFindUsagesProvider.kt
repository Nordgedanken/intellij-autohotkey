package de.nordgedanken.auto_hotkey

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.psi.AHKProperty
import de.nordgedanken.auto_hotkey.psi.AHKTypes
import de.nordgedanken.auto_hotkey.psi.ext.getKey
import de.nordgedanken.auto_hotkey.psi.ext.getValue

class AHKFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner? {
        return DefaultWordsScanner(AHKLexer(),
                TokenSet.create(AHKTypes.KEY),
                TokenSet.create(AHKTypes.COMMENT),
                TokenSet.EMPTY)
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement is PsiNamedElement
    }

    override fun getHelpId(psiElement: PsiElement): String? {
        return null
    }

    override fun getType(element: PsiElement): String {
        return if (element is AHKProperty) {
            "ahk property"
        } else {
            ""
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        return if (element is AHKProperty) {
            element.getKey()
        } else {
            ""
        }
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return if (element is AHKProperty) {
            element.getKey() + element.getValue()
        } else {
            ""
        }
    }
}
