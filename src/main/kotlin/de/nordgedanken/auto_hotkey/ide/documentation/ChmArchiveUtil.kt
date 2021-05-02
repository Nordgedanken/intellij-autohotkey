package de.nordgedanken.auto_hotkey.ide.documentation

import com.github.b3er.idea.plugins.arc.browser.formats.SevenZipArchiveFileSystemImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.impl.ArchiveHandler
import com.intellij.openapi.vfs.newvfs.impl.StubVirtualFile
import de.nordgedanken.auto_hotkey.project.settings.defaultAhkSdk
import de.nordgedanken.auto_hotkey.util.AhkBundle

/**
 * Helpers to manipulate the AutoHotkey.chm file
 */
object ChmArchiveUtil {

    fun getChmArchiveHandler(project: Project): ArchiveHandler {
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

    fun getPathInChm(
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
}
