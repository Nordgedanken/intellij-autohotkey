package de.nordgedanken.auto_hotkey.ide.documentation

import com.github.b3er.idea.plugins.arc.browser.formats.SevenZipArchiveFileSystemImpl
import com.intellij.ide.BrowserUtil
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.lang.documentation.ExternalDocumentationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileTooBigException
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.impl.StubVirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.tree.LeafPsiElement
import de.nordgedanken.auto_hotkey.lang.psi.AhkTokenType
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.util.AhkBundle
import java.io.FileNotFoundException

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
    ): PsiElement? {
        return contextElement
    }

    override fun getUrlFor(element: PsiElement?, originalElement: PsiElement?): MutableList<String>? {
        val url = getUrl(element) ?: return null
        return mutableListOf(url)
    }

    private fun getUrl(element: PsiElement?): String? {
        element ?: return null

        val chm = try {
            getChmArchiveHandler(element.project)
        } catch (e: IllegalStateException) {
            return null
        }

        if (element.text.startsWith("A_"))
            return "https://www.autohotkey.com/docs/Variables.htm#" + element.text.drop(2)

        val pathInChm = getPathInChm(chm, element.text)
            ?: return null

        return "https://www.autohotkey.com/$pathInChm"
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element !is LeafPsiElement) return null
        if (element.elementType !is AhkTokenType) return null

        val chm = try {
            getChmArchiveHandler(element.project)
        } catch (e: IllegalStateException) {
            return e.message
        }

        if (element.text.startsWith("A_"))
            return extractVariableText(chm, element.text)

        val pathInChm = getPathInChm(chm, element.text)
            ?: return null

        return extractHtmlText(chm, pathInChm)
    }

    private fun extractVariableText(chm: ArchiveHandler, variableName: String): String? {
        val variablesPage = getPathInChm(chm, "Variables")
            ?: return null

        return try {
            String(chm.contentsToByteArray(variablesPage))
                .substringAfter("<td>$variableName</td>")
                .substringBefore("</tr>")
                .replace("<td>", "<p>")
                .replace("</td>", "</p>")
                // disable anchor links
                .replace("<a href=\"#", "<span href=\"")
        } catch (e: FileNotFoundException) {
            "File not found"
        } catch (e: FileTooBigException) {
            "File too big"
        }

    }

    private fun extractHtmlText(
        chm: ArchiveHandler,
        pathInChm: String
    ): String {
        return try {
            String(chm.contentsToByteArray(pathInChm))
                // disable anchor links
                .replace("<a href=\"#", "<span href=\"")
        } catch (e: FileNotFoundException) {
            "File not found"
        } catch (e: FileTooBigException) {
            "File too big"
        }
    }

    private fun getPathInChm(
        chm: ArchiveHandler,
        approximateTitle: String?
    ): String? {
        val paths = arrayOf(
            "docs/commands",
            "docs/misc",
            "docs/objects",
            "docs",
            "docs/scripts"
        )

        for (path in paths) {
            val files = chm.list(path)
            for (fileName in files) {
                if (fileName.equals("$approximateTitle.htm", true) ||
                    fileName.equals("_$approximateTitle.htm", true)
                )
                    return "$path/$fileName"
            }
        }
        return null
    }

    private fun getChmArchiveHandler(project: Project): ArchiveHandler {
        val ahkSdk = project.defaultAhkSdk
            ?: error(AhkBundle.msg("documentation.error.no.runner.configured"))

        val homeDirectory: VirtualFile = ahkSdk.homeDirectory
            ?: error(AhkBundle.msg("documentation.error.cannot.access.runner.home.dir"))

        val chmFile: VirtualFile = homeDirectory.findFileByRelativePath("AutoHotkey.chm")
            ?: error(AhkBundle.msg("documentation.error.chm.file.not.found.in.home.dir"))

        val stub = object : StubVirtualFile() {
            override fun getPath(): String = "${chmFile.path}!/"
            override fun getParent(): VirtualFile? = null
        }

        return SevenZipArchiveFileSystemImpl.instance.getHandlerForFile(stub)
            ?: error("Error initializing 7zip file system")
    }

    override fun handleExternal(
        element: PsiElement?,
        originalElement: PsiElement?
    ): Boolean {
        val url = getUrl(element) ?: return false
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
            getChmArchiveHandler(element.project)
        } catch (e: IllegalStateException) {
            return e.message ?: "Error fetching documentation"
        }

        val page = link.substringAfterLast('/')
            .substringBefore('#')
            .substringBeforeLast('.')

        val pathInChm = getPathInChm(chm, page)
            ?: return "Cannot find file in chm file for $page"

        return extractHtmlText(chm, pathInChm)
    }
}
