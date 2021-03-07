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

%state CHECK_FOR_COMMENTS, EXPRESSION, POSSIBLE_EOL_COMMENT

%%
<YYINITIAL> {
    {WHITESPACE_HOZ}    { return TokenType.WHITE_SPACE; }
    "*/"                { return BLOCK_COMMENT; }           // only occurs if we don't match a full block comment
                                                            // (see block_comment.ahk for explanation)
    [^]                 {
                            yypushback(1);                  // cancel parsed char
                            yybegin(CHECK_FOR_COMMENTS);    // and try to parse it again in a different state
                        }
}

<CHECK_FOR_COMMENTS> {
    {LINE_COMMENT}	    { return LINE_COMMENT; }    // only CRLF can follow this
    {BLOCK_COMMENT}	    { return BLOCK_COMMENT; }
    {CRLF}              {
                            yybegin(YYINITIAL);
                            return CRLF;
                        }
    [^]                 {
                            yypushback(1);       // cancel parsed char
                            yybegin(EXPRESSION); // and try to parse it again in a different state
                        }
}

<EXPRESSION> {
    {CHAR_SPECIAL}      { return CHAR_SPECIAL; }
    {TEXT}              { return TEXT; }
    {WHITESPACE_HOZ}    {
                            yybegin(POSSIBLE_EOL_COMMENT);
                            return TokenType.WHITE_SPACE;
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
