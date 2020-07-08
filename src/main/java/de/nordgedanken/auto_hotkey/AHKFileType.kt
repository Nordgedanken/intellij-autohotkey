package de.nordgedanken.auto_hotkey

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object AHKFileType : LanguageFileType(AHKLanguage) {

    override fun getName(): String = "AHK script"

    override fun getIcon(): Icon = AHKIcons.FILE

    override fun getDefaultExtension(): String = "ahk"

    override fun getCharset(file: VirtualFile, content: ByteArray): String = "UTF-8"

    override fun getDescription(): String = "AutoHotKey script file"
}
