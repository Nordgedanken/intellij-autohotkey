package de.nordgedanken.auto_hotkey.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiParserFacade
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.LocalTimeCounter
import de.nordgedanken.auto_hotkey.AHKFileType
import de.nordgedanken.auto_hotkey.psi.ext.AHKElement


class AHKPsiFactory(
        private val project: Project,
        private val markGenerated: Boolean = true,
        private val eventSystemEnabled: Boolean = false
) {
    fun createFile(text: CharSequence): AHKFile =
            PsiFileFactory.getInstance(project)
                    .createFileFromText(
                            "DUMMY.shk",
                            AHKFileType,
                            text,
                            /*modificationStamp =*/ LocalTimeCounter.currentTime(), // default value
                            /*eventSystemEnabled =*/ eventSystemEnabled, // `false` by default
                            /*markAsCopy =*/ markGenerated // `true` by default
                    ) as AHKFile

    fun createNewline(): PsiElement = createWhitespace("\n")
    fun createWhitespace(ws: String): PsiElement =
            PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText(ws)

    fun createFunction(
            text: String
    ): AHKFunction =
            createFromText(text)
                    ?: error("Failed to create function element: text")

    private inline fun <reified T : AHKElement> createFromText(text: String): T? =
            PsiFileFactory.getInstance(project)
                    .createFileFromText("DUMMY.rs", AHKFileType, text)
                    .descendantOfTypeStrict<T>()


}

private fun String.iff(cond: Boolean) = if (cond) "$this " else " "

private inline fun <reified T : PsiElement> PsiElement.descendantOfTypeStrict(): T? =
        PsiTreeUtil.findChildOfType(this, T::class.java, /* strict */ true)
