package de.nordgedanken.auto_hotkey.ide.commenter

import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction
import de.nordgedanken.auto_hotkey.AhkBasePlatformTestCase
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType

class AhkCommenterTest : AhkBasePlatformTestCase() {
    fun testGetLineCommentPrefix() {
        myFixture.configureByText(AhkFileType, " M<caret>sgBox hi")
        CommentByLineCommentAction().run {
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult("; MsgBox hi")
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult(" MsgBox hi")
        }
    }
}
