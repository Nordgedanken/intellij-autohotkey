package de.nordgedanken.auto_hotkey.ide.highlighter

import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType
import de.nordgedanken.auto_hotkey.util.AhkConstants
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
