package de.nordgedanken.auto_hotkey.util

import com.intellij.openapi.util.IconLoader

object AhkIcons {
    @JvmField val FILE = IconLoader.getIcon("/icons/logo.svg", AhkIcons::class.java)
    @JvmField val EXE = IconLoader.getIcon("/icons/ahk_exe.png", AhkIcons::class.java)
}
