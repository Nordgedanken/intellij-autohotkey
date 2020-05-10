package de.nordgedanken.auto_hotkey

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import de.nordgedanken.auto_hotkey.psi.AHKFile
import de.nordgedanken.auto_hotkey.psi.AHKProperty
import de.nordgedanken.auto_hotkey.psi.ext.getKey
import java.util.*

object AHKUtil {
    // Searches the entire project for Simple language files with instances of the Simple property with the given key
    fun findProperties(project: Project?, key: String): List<AHKProperty> {
        val result: MutableList<AHKProperty> = ArrayList()
        val virtualFiles = FileTypeIndex.getFiles(AHKFileType, GlobalSearchScope.allScope(project!!))
        for (virtualFile in virtualFiles) {
            val simpleFile = PsiManager.getInstance(project).findFile(virtualFile!!) as AHKFile?
            if (simpleFile != null) {
                val properties = PsiTreeUtil.getChildrenOfType(simpleFile, AHKProperty::class.java)
                if (properties != null) {
                    for (property in properties) {
                        if (property != null) {
                            if (key == property.getKey()) {
                                result.add(property)
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    fun findProperties(project: Project?): List<AHKProperty> {
        val result: MutableList<AHKProperty> = ArrayList()
        val virtualFiles = FileTypeIndex.getFiles(AHKFileType, GlobalSearchScope.allScope(project!!))
        for (virtualFile in virtualFiles) {
            val simpleFile = PsiManager.getInstance(project).findFile(virtualFile!!) as AHKFile?
            if (simpleFile != null) {
                val properties: Array<AHKProperty?>? = PsiTreeUtil.getChildrenOfType(simpleFile, AHKProperty::class.java)
                if (properties != null) {
                    result.addAll(properties.map {
                        it!!
                    })
                }
            }
        }
        return result
    }
}
