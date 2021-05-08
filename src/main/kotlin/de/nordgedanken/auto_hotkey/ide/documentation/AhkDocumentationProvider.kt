package de.nordgedanken.auto_hotkey.ide.documentation

import com.intellij.ide.BrowserUtil
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.lang.documentation.ExternalDocumentationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.tree.LeafPsiElement
import de.nordgedanken.auto_hotkey.lang.psi.AhkTokenType
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.sdk.ahkDocumentationUrl

/**
 * Provides commands/directives documentation by extracting them from the
 * AutoHotkey.chm help file present in the AutoHotkey home directory
 */
class AhkDocumentationProvider : DocumentationProvider, ExternalDocumentationHandler {

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? = contextElement

    /**
     * Note: External reference links will not show at the bottom of the documentation popup for the simple "quick docs"
     * that appear on mouse hover. You must intentionally execute the View->Quick Documentation action to generate the
     * external reference URL.
     */
    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): MutableList<String>? {
        val url = generateAhkDocumentationReferenceUrlFor(element) ?: return null
        return mutableListOf(url)
    }

    private fun generateAhkDocumentationReferenceUrlFor(element: PsiElement?): String? {
        element ?: return null

        val chm = try {
            ChmArchiveUtil.getChmArchiveHandler(element.project)
        } catch (e: IllegalStateException) {
            return null
        }

        if (element.text.startsWith("A_")) {
            return "${element.project.defaultAhkSdk!!.ahkDocumentationUrl}/docs/Variables.htm#" + element.text.drop(2)
        }

        val pathInChm = ChmArchiveUtil.getPathInChm(chm, element.text)
            ?: return null

        return "${element.project.defaultAhkSdk!!.ahkDocumentationUrl}/$pathInChm"
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element !is LeafPsiElement) return null
        if (element.elementType !is AhkTokenType) return null

        val chm = try {
            ChmArchiveUtil.getChmArchiveHandler(element.project)
        } catch (e: IllegalStateException) {
            return e.message
        }

        if (element.text.startsWith("A_")) {
            return extractVariableText(chm, element.text)
        }

        val pathInChm = ChmArchiveUtil.getPathInChm(chm, element.text)
            ?: return null

        return extractHtmlText(chm, pathInChm)
    }

    private fun extractVariableText(chm: ArchiveHandler, variableName: String): String? = runCatching {
        val variablesPage = ChmArchiveUtil.getPathInChm(chm, "Variables")
            ?: return null

        String(chm.contentsToByteArray(variablesPage))
            .substringAfter("<td>$variableName</td>")
            .substringBefore("</tr>")
            .replace("<td>", "<p>")
            .replace("</td>", "</p>")
            // disable anchor links
            .replace("<a href=\"#", "<span href=\"")
    }.onFailure { it.toString() }.getOrNull()

    private fun extractHtmlText(chm: ArchiveHandler, pathInChm: String) = runCatching {
        String(chm.contentsToByteArray(pathInChm))
            // disable anchor links
            .replace("<a href=\"#", "<span href=\"")
    }.onFailure { it.toString() }.getOrThrow()

    override fun handleExternal(element: PsiElement?, originalElement: PsiElement?): Boolean {
        val url = generateAhkDocumentationReferenceUrlFor(element) ?: return false
        BrowserUtil.browse(url)
        return true
    }

    override fun handleExternalLink(
        psiManager: PsiManager?,
        link: String?,
        context: PsiElement?
    ): Boolean {
        if (link?.startsWith("http") == true) {
            BrowserUtil.browse(link)
        }
        return true
    }

    override fun canFetchDocumentationLink(link: String?): Boolean =
        link != null && !link.startsWith("#") && !link.startsWith("http")

    override fun fetchExternalDocumentation(link: String, element: PsiElement?): String {
        element ?: return "Cannot fetch documentation"

        val chm = try {
            ChmArchiveUtil.getChmArchiveHandler(element.project)
        } catch (e: IllegalStateException) {
            return e.message ?: "Error fetching documentation"
        }

        val page = link.substringAfterLast('/')
            .substringBefore('#')
            .substringBeforeLast('.')

        val pathInChm = ChmArchiveUtil.getPathInChm(chm, page)
            ?: return "Cannot find file in chm file for $page"

        return extractHtmlText(chm, pathInChm)
    }
}
