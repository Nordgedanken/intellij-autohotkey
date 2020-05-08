package de.nordgedanken.auto_hotkey.psi.ext


import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import de.nordgedanken.auto_hotkey.psi.AHKNamedElement


abstract class AHKNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), AHKNamedElement
