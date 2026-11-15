package com.autohotkey.lang.core

import com.autohotkey.util.AhkConstants
import com.autohotkey.util.AhkIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object Ahk2FileType : LanguageFileType(Ahk2Language) {
    override fun getName() = "${AhkConstants.LANGUAGE_NAME} v2"

    override fun getIcon(): Icon = AhkIcons.FILE

    override fun getDefaultExtension() = AhkConstants.FILE_EXTENSION_V2

    override fun getCharset(
        file: VirtualFile,
        content: ByteArray,
    ) = "UTF-8"

    override fun getDescription() = "${AhkConstants.LANGUAGE_NAME} v2 script file"
}
