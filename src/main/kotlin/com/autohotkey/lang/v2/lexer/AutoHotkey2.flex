package com.autohotkey.lang.v2;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static com.autohotkey.lang.v2.psi.Ahk2Types.*;
import static com.intellij.psi.TokenType.*;

%%

%public
%class Ahk2Lexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

// The lexer only splits the text into tokens. What they mean (hotkey, directive, etc) is decided by the bnf.
// The only context the lexer tracks is where comments can start.

//Ahk-specific
CHAR_SPECIAL=[^\s[:letter:][:digit:]]

//generic
WS_HOZ=[ \t]+
CRLF=\R
LINE_COMMENT=;[^\r\n]*
IDENTIFIER=\w+

%state LINE, POSSIBLE_EOL_COMMENT

%%

// Beginning of line: a comment is valid here (after optional whitespace)
<YYINITIAL> {
    {WS_HOZ} { return WS_HOZ; }
    {LINE_COMMENT} { return LINE_COMMENT; }
    {CRLF} { return CRLF; }
    [^] { yypushback(1); yybegin(LINE); }
}

// Anywhere else in a line. A ; is not a comment here since it must be preceded by whitespace
<LINE> {
    "#" { return HASH; }
    "," { return COMMA; }
    "`" { return BACKTICK; }
    "::" { return COLONCOLON; }
    ":=" { return COLONEQUAL; }
    ":" { return COLON; }
    {IDENTIFIER} { return IDENTIFIER; }
    {CHAR_SPECIAL} { return CHAR_SPECIAL; }
    {WS_HOZ} { yybegin(POSSIBLE_EOL_COMMENT); return WS_HOZ; }
    {CRLF} { yybegin(YYINITIAL); return CRLF; }
}

// After whitespace within a line, where a line comment can start
<POSSIBLE_EOL_COMMENT> {
    {LINE_COMMENT} { yybegin(LINE); return LINE_COMMENT; }
    [^] { yypushback(1); yybegin(LINE); }
}

[^] { return BAD_CHARACTER; }
