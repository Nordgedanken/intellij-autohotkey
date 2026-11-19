package com.autohotkey.ide.highlighter

import com.autohotkey.lang.v2.psi.Ahk2Types
import kotlin.test.Test
import kotlin.test.assertEquals

class AhkSyntaxHighlighterTest {
    @Test
    fun `v2 line comments use the comment color`() {
        assertEquals(
            AhkHighlighterColor.LINE_COMMENT,
            AhkSyntaxHighlighter.map(Ahk2Types.LINE_COMMENT),
        )
    }
}
