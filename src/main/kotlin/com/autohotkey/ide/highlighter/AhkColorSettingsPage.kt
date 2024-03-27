package com.autohotkey.ide.highlighter

import com.autohotkey.util.AhkConstants
import com.autohotkey.util.AhkIcons
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.io.StreamUtil
import javax.swing.Icon

class AhkColorSettingsPage : ColorSettingsPage {
    private val attrs = AhkHighlighterColor.values().map { it.attributesDescriptor }.toTypedArray()
    private val annotatorTags = AhkHighlighterColor.values().associateBy({ it.name }, { it.textAttributesKey })
    private val ahkDemoText by lazy {
        val stream = javaClass.getResourceAsStream("demo_text_for_color_settings_page.ahk")!!
        String(StreamUtil.readTextAndConvertSeparators(stream.reader()))
    }

    override fun getDisplayName() = AhkConstants.LANGUAGE_NAME

    override fun getDemoText() = ahkDemoText

    override fun getIcon(): Icon = AhkIcons.LOGO

    override fun getAttributeDescriptors() = attrs

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getHighlighter() = AhkSyntaxHighlighter()

    override fun getAdditionalHighlightingTagToDescriptorMap() = annotatorTags
}
