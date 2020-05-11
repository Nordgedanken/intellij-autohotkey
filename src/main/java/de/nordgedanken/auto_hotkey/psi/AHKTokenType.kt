package de.nordgedanken.auto_hotkey.psi;

import com.intellij.psi.tree.IElementType
import de.nordgedanken.auto_hotkey.AHKLanguage
import de.nordgedanken.auto_hotkey.parser.AHKParserDefinition.Companion.BLOCK_COMMENT
import de.nordgedanken.auto_hotkey.parser.AHKParserDefinition.Companion.EOL_COMMENT
import de.nordgedanken.auto_hotkey.psi.AHKTypes.BLOCK_EXPR
import de.nordgedanken.auto_hotkey.psi.AHKTypes.FUNCTION
import de.nordgedanken.auto_hotkey.psi.ext.tokenSetOf

val AHK_ITEMS = tokenSetOf(
        FUNCTION
)
val AHK_BLOCK_LIKE_EXPRESSIONS = tokenSetOf(BLOCK_EXPR)


open class AHKTokenType(debugName: String) : IElementType(debugName, AHKLanguage)

val AHK_COMMENTS = tokenSetOf(BLOCK_COMMENT, EOL_COMMENT)
