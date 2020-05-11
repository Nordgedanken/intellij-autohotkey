package de.nordgedanken.auto_hotkey.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.messages.Topic


val AHK_STRUCTURE_CHANGE_TOPIC: Topic<AHKStructureChangeListener> = Topic.create(
        "AHK_STRUCTURE_CHANGE_TOPIC",
        AHKStructureChangeListener::class.java,
        Topic.BroadcastDirection.TO_PARENT
)

val AHK_PSI_CHANGE_TOPIC: Topic<AHKPsiChangeListener> = Topic.create(
        "AHK_PSI_CHANGE_TOPIC",
        AHKPsiChangeListener::class.java,
        Topic.BroadcastDirection.TO_PARENT
)


interface AHKStructureChangeListener {
    fun ahkStructureChanged(file: PsiFile?, changedElement: PsiElement?)
}

interface AHKPsiChangeListener {
    fun ahkPsiChanged(file: PsiFile, element: PsiElement, isStructureModification: Boolean)
}
