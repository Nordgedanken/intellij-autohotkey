package de.nordgedanken.auto_hotkey;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static de.nordgedanken.auto_hotkey.lang.psi.AhkTypes.*;
import static com.intellij.psi.TokenType.*;

%%

%public
%class AhkLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

//Ahk-specific
CHAR_SPECIAL=[^\s[:letter:][:digit:]]
TEXT=\w+

//generic
WHITESPACE_HOZ=\p{Blank}+
CRLF=\R
LINE_COMMENT=;.*

%state POSSIBLE_EOL_COMMENT

%%
<YYINITIAL> {
	^{LINE_COMMENT}	    { return LINE_COMMENT; }
	{CHAR_SPECIAL}      { return CHAR_SPECIAL; }
	{TEXT}              { return TEXT; }
	{WHITESPACE_HOZ}    {
							yybegin(POSSIBLE_EOL_COMMENT);
							return WHITESPACE_HOZ;
						}
	{CRLF}              { return CRLF; }
}

<POSSIBLE_EOL_COMMENT> {
	{LINE_COMMENT}      { return LINE_COMMENT; }
	[^]                 {
							yypushback(1);      // cancel parsed char (no comment typed here)
							yybegin(YYINITIAL); // and try to parse it again in <YYINITIAL>
						}
}

[^] { return BAD_CHARACTER; }
