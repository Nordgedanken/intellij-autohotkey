package com.autohotkey.ide.commenter

import com.autohotkey.AhkBasePlatformTestCase
import com.autohotkey.lang.core.AhkFileType
import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction

class AhkCommenterTest : AhkBasePlatformTestCase() {
    fun `test line comment added to caret's line with no selection`() {
        myFixture.configureByText(AhkFileType, " M<caret>sgBox hi") // extra space before 'M' on purpose
        CommentByLineCommentAction().run {
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult("; MsgBox hi")
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult(" MsgBox hi")
        }
    }

    fun `test line comments added to all lines in selection`() {
        myFixture.configureByText(AhkFileType, " M<selection>sgBox hi\nMsgBox h</selection>ey")
        CommentByLineCommentAction().run {
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult("; M<selection>sgBox hi\n;MsgBox h</selection>ey")
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult(" M<selection>sgBox hi\nMsgBox h</selection>ey")
        }
    }

    fun `test block comment surrounds all lines in selection and whole comment is then selected`() {
        myFixture.configureByText(AhkFileType, " M<selection>sgBox hi\nMsgBox h</selection>ey")
        CommentByBlockCommentAction().run {
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult("<selection>/* MsgBox hi\nMsgBox hey\n*/</selection>")
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult(" MsgBox hi\nMsgBox hey")
        }
    }

    fun `test block comment surrounds caret's line w no selection and nothing selected`() {
        myFixture.configureByText(AhkFileType, " MsgBox <caret>hi\nMsgBox hey")
        CommentByBlockCommentAction().run {
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult("/* MsgBox <caret>hi\n*/\nMsgBox hey")
            actionPerformedImpl(project, myFixture.editor)
            myFixture.checkResult(" MsgBox <caret>hi\nMsgBox hey")
        }
    }
}
