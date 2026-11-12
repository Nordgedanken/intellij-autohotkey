package com.autohotkey.ide.documentation

import com.autohotkey.project.settings.defaultAhkSdk
import com.autohotkey.util.AhkBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jchmlib.ChmFile
import org.jchmlib.ChmStopEnumeration
import org.jchmlib.ChmUnitInfo
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * Helpers to manipulate the AutoHotkey.chm file
 */
object ChmArchiveUtil {
    private val chmFiles = ConcurrentHashMap<String, ChmFile>()

    fun getChmFile(project: Project): ChmFile {
        val ahkSdk =
            project.defaultAhkSdk
                ?: error(AhkBundle.msg("documentation.error.no.runner.configured"))

        val homeDirectory: VirtualFile =
            ahkSdk.homeDirectory
                ?: error(AhkBundle.msg("documentation.error.cannot.access.runner.home.dir"))

        val chmFile: VirtualFile =
            homeDirectory.findFileByRelativePath("AutoHotkey.chm")
                ?: error(AhkBundle.msg("documentation.error.chm.file.not.found.in.home.dir"))

        return chmFiles.computeIfAbsent(chmFile.path) {
            try {
                ChmFile(it)
            } catch (e: IOException) {
                throw IllegalStateException(e.message ?: "Unable to open AutoHotkey.chm", e)
            }
        }
    }

    fun getPathInChm(
        chm: ChmFile,
        approximateTitle: String?,
    ): String? {
        val paths =
            arrayOf(
                "docs/lib",
                "docs/commands",
                "docs/misc",
                "docs/objects",
                "docs",
                "docs/scripts",
            )

        for (path in paths) {
            var matchingPath: String? = null
            chm.enumerateDir(
                "/$path/",
                ChmFile.CHM_ENUMERATE_NORMAL or ChmFile.CHM_ENUMERATE_FILES,
            ) { entry: ChmUnitInfo ->
                val fileName = entry.path.substringAfterLast('/')
                if (fileName.equals("$approximateTitle.htm", true) ||
                    fileName.equals("_$approximateTitle.htm", true)
                ) {
                    matchingPath = entry.path.removePrefix("/")
                    throw ChmStopEnumeration()
                }
            }
            if (matchingPath != null) return matchingPath
        }
        return null
    }
}
