package com.autohotkey.ide.highlighter

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.lang.core.AhkFileType
import com.autohotkey.util.AhkConstants
import org.intellij.lang.annotations.Language

class AhkHighlightAnnotatorTest : AhkBasePlatformTestCase() {
    fun `test directive is highlighted`() = checkInfoHighlighting(
        """<info descr="null">#Warn</info>"""
    )

    fun `test hotkey is highlighted`() = checkInfoHighlighting(
        """<info descr="null">^a</info>::"""
    )

    private fun checkInfoHighlighting(@Language(AhkConstants.LANGUAGE_NAME) ahkCodeWAnnotationInfo: String) {
        myFixture.configureByText(AhkFileType, ahkCodeWAnnotationInfo)
        myFixture.checkHighlighting(false, true, false)
    }
}
