package com.autohotkey.ide.highlighter

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.lang.core.Ahk2FileType

class Ahk2HighlightAnnotatorTest : AhkBasePlatformTestCase() {
    fun `test directive is highlighted`() = checkInfoHighlighting("""<info descr="null">#Warn</info> All""")

    fun `test hotkey is highlighted`() = checkInfoHighlighting("""<info descr="null">^a::</info>MsgBox hi""")

    fun `test hotstring is highlighted`() = checkInfoHighlighting("""<info descr="null">:*:btw::</info>by the way""")

    fun `test directive with comma is not highlighted`() = checkInfoHighlighting("#Warn, All")

    private fun checkInfoHighlighting(ahkCodeWAnnotationInfo: String) {
        myFixture.configureByText(Ahk2FileType, ahkCodeWAnnotationInfo)
        myFixture.checkHighlighting(false, true, false)
    }
}
