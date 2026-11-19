package com.autohotkey.lang.v2.psi

import com.autohotkey.lang.core.Ahk2Language
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class Ahk2ElementType(
    @NonNls debugName: String,
) : IElementType(debugName, Ahk2Language)
