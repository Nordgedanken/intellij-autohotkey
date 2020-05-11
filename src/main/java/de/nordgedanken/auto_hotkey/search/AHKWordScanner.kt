package de.nordgedanken.auto_hotkey.search

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.psi.tree.TokenSet
import de.nordgedanken.auto_hotkey.AutoHotKey.flex.AHKLexer
import de.nordgedanken.auto_hotkey.psi.AHKTypes.IDENTIFIER
import de.nordgedanken.auto_hotkey.psi.AHKTypes.STRING_LITERAL
import de.nordgedanken.auto_hotkey.psi.AHK_COMMENTS

class AHKWordScanner : DefaultWordsScanner(
        AHKLexer(),
        TokenSet.create(IDENTIFIER),
        AHK_COMMENTS,
        TokenSet.create(STRING_LITERAL)
)
