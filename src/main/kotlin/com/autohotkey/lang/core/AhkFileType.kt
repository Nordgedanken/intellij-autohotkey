package com.autohotkey.lang.core

import com.autohotkey.util.AhkConstants
import com.autohotkey.util.AhkIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object AhkFileType : LanguageFileType(AhkLanguage) {
    override fun getName() = AhkConstants.LANGUAGE_NAME

    override fun getIcon(): Icon = AhkIcons.FILE

    override fun getDefaultExtension() = AhkConstants.FILE_EXTENSION

    override fun getCharset(file: VirtualFile, content: ByteArray) = "UTF-8"

    override fun getDescription() = "${AhkConstants.LANGUAGE_NAME} script file"
}

fun VirtualFile.isAhkFile(): Boolean = fileType is AhkFileType
