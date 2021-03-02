package de.nordgedanken.auto_hotkey.ide.highlighter

import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import de.nordgedanken.auto_hotkey.util.AhkIcons
import javax.swing.Icon

class AhkColorSettingsPage : ColorSettingsPage {
    private val ATTRS = AhkColor.values().map { it.attributesDescriptor }.toTypedArray()

    private val ANNOTATOR_TAGS = AhkColor.values().associateBy({ it.name }, { it.textAttributesKey })

    override fun getIcon(): Icon = AhkIcons.FILE

    override fun getHighlighter() = AhkSyntaxHighlighter()

    override fun getDemoText(): String {
        return """
#Include current_url.ahk
Menu, Tray, Icon, % A_WinDir "\system32\netshell.dll" , 86 ; Shows a world icon in the system tray

ModernBrowsers := "ApplicationFrameWindow,Chrome_WidgetWin_0,Chrome_WidgetWin_1,Maxthon3Cls_MainFrm,MozillaWindowClass,Slimjet_WidgetWin_1"
LegacyBrowsers := "IEFrame,OperaWindowClass"

;^+!u:: 
;	nTime := A_TickCount
;	sURL := GetActiveBrowserURL()
;	WinGetClass, sClass, A
;	If (sURL != "")
;		MsgBox, % "The URL is  sURL`nEllapsed time: " (A_TickCount - nTime) " ms (" sClass ")"
;	Else If sClass In % ModernBrowsers "," LegacyBrowsers
;		MsgBox, % "The URL couldn't be determined (" sClass ")"
;	Else
;		MsgBox, % "Not a browser or browser not supported (" sClass ")"
;Return


#c::
clipboard := GetActiveBrowserURL()
Return"""
    }

    override fun getAdditionalHighlightingTagToDescriptorMap() = ANNOTATOR_TAGS

    override fun getAttributeDescriptors() = ATTRS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = "AutoHotkey"
}
