package de.nordgedanken.auto_hotkey.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import de.nordgedanken.auto_hotkey.AHKFileType


object AHKElementFactory {
    fun createProperty(project: Project?, name: String?): AHKProperty {
        val file: AHKFile = createFile(project, name)
        return file.firstChild as AHKProperty
    }

    private fun createFile(project: Project?, text: String?): AHKFile {
        val name = "dummy.ahk"
        return PsiFileFactory.getInstance(project).createFileFromText(name, AHKFileType.INSTANCE, text!!) as AHKFile
    }
}
