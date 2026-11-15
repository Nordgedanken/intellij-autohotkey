package com.autohotkey.lang.core

import com.intellij.lang.Language

object Ahk2Language : Language(AhkLanguage, "AutoHotkey2") {
    private fun readResolve(): Any = Ahk2Language
}
