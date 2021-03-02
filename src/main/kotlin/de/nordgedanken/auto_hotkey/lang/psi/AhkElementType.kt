package de.nordgedanken.auto_hotkey.lang.psi

import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.lang.core.AhkLanguage
import org.jetbrains.annotations.NonNls

class AhkElementType(@NonNls debugName: String) : IElementType(debugName, AhkLanguage)
