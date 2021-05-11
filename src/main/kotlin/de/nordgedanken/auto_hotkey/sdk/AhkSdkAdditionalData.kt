package de.nordgedanken.auto_hotkey.sdk

import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element

const val DEFAULT_AHK_EXE_NAME = "AutoHotkey.exe"

/**
 * Contains the additional data associated with an Ahk sdk.
 *
 * Currently, we are storing:
 * - the ahk executable name that the user selects while creating the sdk
 */
data class AhkSdkAdditionalData(var exeName: String = DEFAULT_AHK_EXE_NAME) : SdkAdditionalData {
    fun writeTo(element: Element) {
        JDOMExternalizerUtil.writeField(element, ::exeName.name, exeName)
    }

    companion object {
        fun generateFrom(element: Element): AhkSdkAdditionalData {
            val exeName = JDOMExternalizerUtil.readField(element, AhkSdkAdditionalData::exeName.name)
            return AhkSdkAdditionalData(exeName ?: DEFAULT_AHK_EXE_NAME)
        }
    }
}
