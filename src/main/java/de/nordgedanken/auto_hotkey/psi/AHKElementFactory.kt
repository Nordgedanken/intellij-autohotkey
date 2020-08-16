package de.nordgedanken.auto_hotkey.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType


object AHKElementFactory {
    fun createProperty(project: Project?, name: String?): AHKVariable {
        val file: AHKFile = createFile(project, name)
        return file.firstChild as AHKVariable
    }

    private fun createFile(project: Project?, text: String?): AHKFile {
        val name = "dummy.ahk"
        return PsiFileFactory.getInstance(project).createFileFromText(name, AhkFileType, text!!) as AHKFile
    }
}
