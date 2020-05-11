package de.nordgedanken.auto_hotkey.psi;

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.AHKLanguage
import de.nordgedanken.auto_hotkey.parser.AHKParserDefinition.Companion.BLOCK_COMMENT
import de.nordgedanken.auto_hotkey.parser.AHKParserDefinition.Companion.EOL_COMMENT
import de.nordgedanken.auto_hotkey.psi.AHKTypes.*

val AHK_ITEMS = tokenSetOf(
        FUNCTION
)
val AHK_BLOCK_LIKE_EXPRESSIONS = tokenSetOf(BLOCK_EXPR)

fun tokenSetOf(vararg tokens: IElementType) = TokenSet.create(*tokens)

open class AHKTokenType(debugName: String) : IElementType(debugName, AHKLanguage)

val AHK_COMMENTS = tokenSetOf(BLOCK_COMMENT, EOL_COMMENT)

val AHK_LITERALS = tokenSetOf(STRING_LITERAL, INTEGER_LITERAL)

val AHK_EOL_COMMENTS = tokenSetOf(EOL_COMMENT)

val AHK_ALL_STRING_LITERALS = tokenSetOf(STRING_LITERAL)
