package com.autohotkey.lang.psi

import com.autohotkey.lang.core.AhkLanguage
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class AhkElementType(@NonNls debugName: String) : IElementType(debugName, AhkLanguage)

fun PsiElement.isLeaf(): Boolean = node.firstChildNode == null
