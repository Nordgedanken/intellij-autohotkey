package de.nordgedanken.auto_hotkey.ide.commenter

import com.intellij.codeInsight.generation.CommenterDataHolder
import com.intellij.codeInsight.generation.SelfManagingCommenter
import com.intellij.codeInsight.generation.SelfManagingCommenterUtil.getBlockCommentRange
import com.intellij.codeInsight.generation.SelfManagingCommenterUtil.insertBlockComment
import com.intellij.codeInsight.generation.SelfManagingCommenterUtil.uncommentBlockComment
import com.intellij.lang.Commenter
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.util.text.CharArrayUtil

/**
 * Defines the behavior that occurs when the user executes one of the 'Toggle comment' actions (or invokes its shortcut)
 *
 * Known Issues:
 * - If you create a block comment, then highlight text within the block comment and toggle again, it will create a new
 * block comment within the original.
 */
class AhkCommenter : Commenter, SelfManagingCommenter<AhkCommentHolder> {
    override fun getLineCommentPrefix() = ";"

    override fun getBlockCommentPrefix(): String = "/*"
    override fun getBlockCommentSuffix(): String = "\n*/"

    // unused since we use SelfManagingCommenter
    override fun getCommentedBlockCommentPrefix(): String = "/*"
    override fun getCommentedBlockCommentSuffix(): String = "*/"

    override fun createLineCommentingState(
        startLine: Int,
        endLine: Int,
        document: Document,
        file: PsiFile
    ) = AhkCommentHolder(false)

    override fun createBlockCommentingState(
        selectionStart: Int,
        selectionEnd: Int,
        document: Document,
        file: PsiFile
    ) = AhkCommentHolder(selectionStart != selectionEnd)

    override fun commentLine(line: Int, offset: Int, document: Document, data: AhkCommentHolder) {
        document.insertString(offset, ";")
    }

    override fun uncommentLine(line: Int, offset: Int, document: Document, data: AhkCommentHolder) {
        document.deleteString(offset, offset + 1)
    }

    /**
     * Checks whether the first non-whitespace char on the line is [AhkCommenter.getLineCommentPrefix]
     */
    override fun isLineCommented(line: Int, offset: Int, document: Document, data: AhkCommentHolder): Boolean {
        val offsetOfLineStart = document.getLineStartOffset(line)
        val chars = document.charsSequence
        return chars[CharArrayUtil.shiftForward(chars, offsetOfLineStart, " \t")] == lineCommentPrefix.single()
    }

    override fun getCommentPrefix(line: Int, document: Document, data: AhkCommentHolder): String = lineCommentPrefix

    override fun getBlockCommentRange(
        selectionStart: Int,
        selectionEnd: Int,
        document: Document,
        data: AhkCommentHolder
    ) = getBlockCommentRange(selectionStart, selectionEnd, document, blockCommentPrefix, blockCommentSuffix)

    override fun getBlockCommentPrefix(selectionStart: Int, document: Document, data: AhkCommentHolder): String =
        blockCommentPrefix

    override fun getBlockCommentSuffix(selectionEnd: Int, document: Document, data: AhkCommentHolder): String =
        blockCommentSuffix

    override fun uncommentBlockComment(startOffset: Int, endOffset: Int, document: Document, data: AhkCommentHolder?) {
        uncommentBlockComment(startOffset, endOffset, document, blockCommentPrefix, blockCommentSuffix)
    }

    /**
     * Determines the line at the start of the selection and calculates the offset for the beginning of the line.
     * Determines the line at the end of the selection and calculates the offset for the end of the line.
     * It then inserts the beginning and ending block comment mark at those locations, respectively.
     *
     * If the user toggled a block comment without highlighting anything, it returns an empty text range so that nothing
     * is highlighted. (This is less annoying since it allows the user to continue typing in the line they
     * block-commented. If users post issues that they want different behavior, we can figure out a different solution)
     */
    override fun insertBlockComment(
        startOffset: Int,
        endOffset: Int,
        document: Document,
        data: AhkCommentHolder?
    ): TextRange {
        val selectionStartLineNum = document.getLineNumber(startOffset)
        val selLineStartOffset = document.getLineStartOffset(selectionStartLineNum)
        val selectionEndLineNum = document.getLineNumber(endOffset)
        val selLineEndOffset = document.getLineEndOffset(selectionEndLineNum)
        return if (data?.isToggledFromSelection == true) {
            insertBlockComment(selLineStartOffset, selLineEndOffset, document, blockCommentPrefix, blockCommentSuffix)
        } else {
            insertBlockComment(selLineStartOffset, selLineEndOffset, document, blockCommentPrefix, blockCommentSuffix)
            return TextRange(0, 0)
        }
    }

    override fun blockCommentRequiresFullLineSelection() = true
}

/**
 * Holds data about the comment that's about to be generated.
 *
 * @param isToggledFromSelection Stores whether or not the user had highlighted a section of code before executing
 * "toggle comments". This is needed for block comment handling- If the user had highlighted a section before toggle,
 * the newly block-commented section will be highlighted after it's created. If the user had not highlighted anything,
 * then the new block comment will be added but nothing will be highlighted
 */
data class AhkCommentHolder(val isToggledFromSelection: Boolean) : CommenterDataHolder()
