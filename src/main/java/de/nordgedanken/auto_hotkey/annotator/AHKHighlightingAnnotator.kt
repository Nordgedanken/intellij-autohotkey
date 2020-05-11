package de.nordgedanken.auto_hotkey.annotator

import com.intellij.ide.annotator.AnnotatorBase
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderEx
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.impl.cache.impl.IndexPatternUtil
import com.intellij.psi.impl.search.PsiTodoSearchHelperImpl
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.IndexPattern
import com.intellij.psi.search.PsiTodoSearchHelper
import com.intellij.psi.util.elementType
import de.nordgedanken.auto_hotkey.colors.AHKColor
import de.nordgedanken.auto_hotkey.psi.AHKFunction
import de.nordgedanken.auto_hotkey.psi.AHKTypes.IDENTIFIER
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement

class AHKHighlightingAnnotator : AnnotatorBase() {
    override fun annotateInternal(element: PsiElement, holder: AnnotationHolder) {
        val color = when (element) {
            is LeafPsiElement -> highlightLeaf(element, holder)
            else -> null
        } ?: return

        val severity = HighlightSeverity.INFORMATION
        // BACKCOMPAT: 2019.3
        @Suppress("DEPRECATION")
        holder.createAnnotation(severity, element.textRange, null).textAttributes = color.textAttributesKey
    }

    private fun highlightLeaf(element: PsiElement, holder: AnnotationHolder): AHKColor? {
        val parent = element.parent as? AHKElement ?: return null

        return when (element.elementType) {
            IDENTIFIER -> highlightIdentifier(element, parent, holder)
            else -> null
        }
    }

    private fun highlightIdentifier(element: PsiElement, parent: AHKElement, holder: AnnotationHolder): AHKColor? {
        return when {
            parent is PsiNameIdentifierOwner && parent.nameIdentifier == element -> {
                colorFor(parent)
            }
            else -> null
        }
    }

    inline fun <T> UserDataHolderEx.getOrPut(key: Key<T>, defaultValue: () -> T): T =
            getUserData(key) ?: putUserDataIfAbsent(key, defaultValue())

    private fun isTodoHighlightingEnabled(file: PsiFile, holder: AnnotationHolder): Boolean {
        return holder.currentAnnotationSession.getOrPut(IS_TODO_HIGHLIGHTING_ENABLED) {
            val helper = PsiTodoSearchHelper.SERVICE.getInstance(file.project) as? PsiTodoSearchHelperImpl
                    ?: return@getOrPut false
            if (!helper.shouldHighlightInEditor(file)) return@getOrPut false
            IndexPatternUtil.getIndexPatterns().any { it.isTodoPattern }
        }
    }

    // It's hacky way to check that pattern is used to find TODOs without real computation
    val IndexPattern.isTodoPattern: Boolean get() = patternString.contains("TODO", true)

    companion object {
        private val IS_TODO_HIGHLIGHTING_ENABLED: Key<Boolean> = Key.create("IS_TODO_HIGHLIGHTING_ENABLED")
    }
}
// If possible, this should use only stubs because this will be called
// on elements in other files when highlighting references.
private fun colorFor(element: AHKElement): AHKColor? = when (element) {
    is AHKFunction -> AHKColor.FUNCTION
    else -> null
}
