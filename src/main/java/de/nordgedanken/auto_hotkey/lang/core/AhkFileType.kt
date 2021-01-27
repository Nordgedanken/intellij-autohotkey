package de.nordgedanken.auto_hotkey.lang.core

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import de.nordgedanken.auto_hotkey.util.AhkConstants
import de.nordgedanken.auto_hotkey.util.AhkIcons
import javax.swing.Icon

object AhkFileType : LanguageFileType(AhkLanguage) {
    override fun getName() = "${AhkConstants.LANGUAGE_NAME} script"

    override fun getIcon(): Icon = AhkIcons.FILE

    override fun getDefaultExtension() = AhkConstants.FILE_EXTENSION

    override fun getCharset(file: VirtualFile, content: ByteArray) = "UTF-8"

    override fun getDescription() = "${AhkConstants.LANGUAGE_NAME} script file"
}
