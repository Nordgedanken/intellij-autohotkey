package com.autohotkey.lang.psi

import com.autohotkey.lang.core.Ahk2FileType
import com.autohotkey.lang.core.Ahk2Language
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class Ahk2File(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, Ahk2Language) {
    override fun getFileType(): FileType = Ahk2FileType

    override fun toString() = "AutoHotkey v2 Script File"
}
