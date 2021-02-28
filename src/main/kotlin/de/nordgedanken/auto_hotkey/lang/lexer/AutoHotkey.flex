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
WHITESPACE_HOZ=\p{Blank}+ //JFlex doesn't support \h
CRLF=\R
LINE_COMMENT=;.*
/*
Basically we parse anything after /\* except a linefeed followed by *\/ since
that indicates the end of the block comment. Note that you can end a script with
an open /\* tag that has no corresponding *\/
*/
BLOCK_COMMENT="/*" !([^]* \R\p{Blank}* "*/" [^]*) (\R\p{Blank}* "*/")?

%state EXPRESSION, POSSIBLE_EOL_COMMENT

%%
<YYINITIAL> {
	{LINE_COMMENT}	    { return LINE_COMMENT; }
	{BLOCK_COMMENT}	    { return BLOCK_COMMENT; }
	{WHITESPACE_HOZ}    { return WHITESPACE_HOZ; }
	{CRLF}              { return CRLF; }
    [^]                 {
                            yypushback(1);       // cancel parsed char
                            yybegin(EXPRESSION); // and try to parse it again in <EXPRESSION>
                        }
}

<EXPRESSION> {
	{CHAR_SPECIAL}      { return CHAR_SPECIAL; }
	{TEXT}              { return TEXT; }
    {WHITESPACE_HOZ}    {
                            yybegin(POSSIBLE_EOL_COMMENT);
                            return WHITESPACE_HOZ;
                        }
    {CRLF}              {
      	                    yybegin(YYINITIAL);
      	                    return CRLF;
                        }
}

<POSSIBLE_EOL_COMMENT> {
	{LINE_COMMENT}      { return LINE_COMMENT; }
	[^]                 {
							yypushback(1);      // cancel parsed char (no comment typed here)
							yybegin(EXPRESSION); // and try to parse it again in <YYINITIAL>
						}
}

[^] { return BAD_CHARACTER; }
