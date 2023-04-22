package com.autohotkey;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static com.autohotkey.lang.psi.AhkTypes.*;
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

//generic
WS_HOZ=\p{Blank}+ //JFlex doesn't support \h
CRLF=\R
LINE_COMMENT=;.*
IDENTIFIER  = \w+

/*
Basically we parse anything after /\* except a linefeed followed by *\/ since
that indicates the end of the block comment. Note that you can end a script with
an open /\* tag that has no corresponding *\/
*/
BLOCK_COMMENT="/*" !([^]* \R\p{Blank}* "*/" [^]*) (\R\p{Blank}* "*/")?

%state CHECK_FOR_BOL_ELEMENTS, LEGACY_MODE, POSSIBLE_EOL_COMMENT

%%
/*
Starting state. Whenever we hit a CRLF, we return back to this state.
*/
<YYINITIAL> {
    {WS_HOZ}    { return WS_HOZ; }
    "*/"                { return BLOCK_COMMENT; }           // only occurs if we don't match a full block comment
                                                            // (see block_comment.ahk for explanation)
    [^]                 {
                            yypushback(1);                  // cancel parsed char
                            yybegin(CHECK_FOR_BOL_ELEMENTS);    // and try to parse it again in a different state
                        }
}

/*
Contains elements which can only validly occur at the beginning of a line.
 */
<CHECK_FOR_BOL_ELEMENTS> {
    {LINE_COMMENT}	    { return LINE_COMMENT; }    // Only CRLF can follow this
    {BLOCK_COMMENT}	    { return BLOCK_COMMENT; }   // Can only occur at beginning of line, not after other chars
    {WS_HOZ}            { return WS_HOZ; }
    {CRLF}              {
                            yybegin(YYINITIAL);
                            return CRLF;
                        }
    [^]                 {
                            yypushback(1);       // cancel parsed char
                            yybegin(LEGACY_MODE); // and try to parse it again in a different state
                        }
}

<LEGACY_MODE> {
    "#"                 { return HASH; }
    ","                 { return COMMA; }
    "`"                 { return BACKTICK; }
    "::"                { return COLONCOLON; }
    ":"                 { return COLON; }
    {IDENTIFIER}        { return IDENTIFIER; }
    {CHAR_SPECIAL}      { return CHAR_SPECIAL; }
    {WS_HOZ}    {
                            yybegin(POSSIBLE_EOL_COMMENT);
                            return WS_HOZ;
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
                            yybegin(LEGACY_MODE); // and try to parse it again in <YYINITIAL>
                        }
}

[^] { return BAD_CHARACTER; }
