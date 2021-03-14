package de.nordgedanken.auto_hotkey.ide.commenter

import com.intellij.lang.Commenter

/**
 * Defines the comment character that should be used when the user executes one
 * of the 'Toggle comment' actions (or invokes its shortcut)
 */
class AhkCommenter : Commenter {
    override fun getLineCommentPrefix() = ";"

    override fun getBlockCommentPrefix(): String? = null

    override fun getBlockCommentSuffix(): String? = null

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null
}
