package com.autohotkey.lang.psi

import com.autohotkey.lang.core.AhkFileType
import com.autohotkey.lang.core.AhkLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class AhkFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, AhkLanguage) {
    override fun getFileType(): FileType = AhkFileType

    override fun toString() = "AutoHotkey Script File"
}
